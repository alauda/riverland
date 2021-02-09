package com.alauda.ms.riverland.trans;

import com.alauda.ms.riverland.robwin.AbstractCircuitBreakerTest;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.collection.Stream;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class BoxTest extends AbstractCircuitBreakerTest {

    private String path = "box/breaker";
    @Test
    public void shouldOpenBackendACircuitBreaker() {
        // When
        Stream.rangeClosed(1,6).forEach((count) -> produceFailure(path));

        // Then
        checkHealthStatus(BACKEND_A, CircuitBreaker.State.OPEN);
    }

    @Test
    public void shouldCloseBackendACircuitBreaker() {
        transitionToOpenState(BACKEND_A);
        circuitBreakerRegistry.circuitBreaker(BACKEND_A).transitionToHalfOpenState();

        // When
        Stream.rangeClosed(1,5).forEach((count) -> produceSuccess(path));

        // Then
        checkHealthStatus(BACKEND_A, CircuitBreaker.State.CLOSED);
    }

    @Test
    public void shouldOpenBackendBCircuitBreaker() {
        // When
        Stream.rangeClosed(1,4).forEach((count) -> produceFailure(path));

        // Then
        checkHealthStatus(BACKEND_B, CircuitBreaker.State.OPEN);
    }

    @Test
    public void shouldCloseBackendBCircuitBreaker() {
        transitionToOpenState(BACKEND_B);
        circuitBreakerRegistry.circuitBreaker(BACKEND_B).transitionToHalfOpenState();

        // When
        Stream.rangeClosed(1,5).forEach((count) -> produceSuccess(path));

        // Then
        checkHealthStatus(BACKEND_B, CircuitBreaker.State.CLOSED);
    }

    private void produceFailure(String backend) {
        ResponseEntity<String> response = restTemplate.getForEntity("/" + backend + "/down", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private void produceSuccess(String backend) {
        ResponseEntity<String> response = restTemplate.getForEntity("/" + backend + "/success", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
