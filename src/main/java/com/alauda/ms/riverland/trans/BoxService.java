package com.alauda.ms.riverland.trans;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public interface BoxService {

    public String circuit();
    public Mono<String> process();
    public Flux<String> fluxTimeout();
    public Mono<String> monoTimeout();
    public Flux<String> fluxSuccess();
    public CompletableFuture<String> futureSuccess();
    public String success();
    public Mono<String> processLess();
    public Mono<String> breakerSuccess();
    public String breakerFailure();

    @CircuitBreaker(name = BoxServiceImpl.BACKEND_A)
    @Bulkhead(name = BoxServiceImpl.BACKEND_A)
    @Retry(name = BoxServiceImpl.BACKEND_A)
    String failure();

    @Retry(name = BoxServiceImpl.BACKEND_A)
    String retrySuccess();

    @Retry(name = BoxServiceImpl.BACKEND_A)
    String retryFailure();
}
