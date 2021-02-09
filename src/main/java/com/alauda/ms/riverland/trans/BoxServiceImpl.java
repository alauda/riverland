package com.alauda.ms.riverland.trans;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BoxServiceImpl implements BoxService {
    private static final Logger log = LoggerFactory.getLogger(BoxServiceImpl.class);
    public static final String BACKEND_A = "backendA";
    private AtomicInteger count = new AtomicInteger();
    String host = "http://localhost:8021";
    @Autowired
    private RestTemplateBuilder builder;

    @Override
    //@CircuitBreaker(name = "backendB", fallbackMethod = "fallback")
    public String circuit() {
        return builder.build()
                .getForObject(host + "/truck/failure", String.class);
    }

    @Override
    @RateLimiter(name="processService", fallbackMethod = "processFallback")
    public Mono<String> process()  {
        return Mono.just("Hello World ...");
    }

    private Mono<String> processFallback(Throwable exc) {
        log.error("eh!!! this is the error {}", exc.getLocalizedMessage());
        return Mono.just("inside from fallback method because " + exc.getLocalizedMessage());
    }

    String fallback() {
        return "Fallback for circuit";
    }

    @Override
    public Flux<String> fluxTimeout() {
        return Flux.just("Hello World from backend B")
                .delayElements(Duration.ofSeconds(10));
    }

    @Override
    public Mono<String> monoTimeout() {
        return Mono.just("Hello World from backend B")
                .delayElement(Duration.ofSeconds(10));
    }

    @Override
    public Flux<String> fluxSuccess() {
        return Flux.just("Hello", "World");
    }

    @Override
    public CompletableFuture<String> futureSuccess() {
        return CompletableFuture.completedFuture("Hello World from backend B");
    }

    @Override
    public String success() {
        return builder
                .build()
                .getForObject(host + "/truck/success", String.class);
    }

    @Override
    @RateLimiter(name= BACKEND_A, fallbackMethod = "processFallback")
    public Mono<String> processLess() {
        builder.build()
                .getForObject(host + "/tweets-blocking", String.class);
        return Mono.just("Hello World ...");
    }

    @Override
    @CircuitBreaker(name = BACKEND_A, fallbackMethod="fallback")
    public Mono<String> breakerSuccess() {
        builder.build()
                .getForObject(host + "/tweets-blocking", String.class);
        return Mono.just("Still Working ...");
    }

    Mono<String> fallback(IOException e) {
        return Mono.just("Fallback IO Exception");
    }

    @Override
    @CircuitBreaker(name = BACKEND_A)
    //@Bulkhead(name = BACKEND_A)
    //@Retry(name = BACKEND_A)
    public String breakerFailure() {
        log.info("Running {}", count.incrementAndGet());
        //throw new IOException("Write Error ...");
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    @CircuitBreaker(name = BACKEND_A)
    @Bulkhead(name = BACKEND_A)
    @Retry(name = BACKEND_A)
    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    @Retry(name = BACKEND_A)
    public String retrySuccess() {
        return ("Hello World With Retry");
    }

    @Override
    @Retry(name = BACKEND_A)
    public String retryFailure() {
        log.info("Current Timestamp for Retry: {}", Instant.now());
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }
}
