package com.alauda.ms.riverland.robwin.controller;

import com.alauda.ms.riverland.robwin.service.Service;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/backendA")
public class BackendAController {

    private final Service businessAService;
    private static final Logger log = LoggerFactory.getLogger(BackendAController.class);
    @Autowired
    RateLimiterRegistry rateLimiterRegistry;

    public BackendAController(@Qualifier("backendAService") Service businessAService){
        this.businessAService = businessAService;
    }

    @GetMapping("failure")
    public String failure(){
        return businessAService.failure();
    }

    @GetMapping("success2")
    @RateLimiter(name = "backendA")
    public String success2(){
        io.github.resilience4j.ratelimiter.RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("backendA");
        CheckedRunnable restrictedCall = io.github.resilience4j.ratelimiter.RateLimiter
                .decorateCheckedRunnable(rateLimiter, businessAService::success);
        CheckedFunction0<String> supplier = io.github.resilience4j.ratelimiter.RateLimiter
                .decorateCheckedSupplier(rateLimiter, () -> {
                    return businessAService.success();
                });
//        Try.run(restrictedCall)
//                .andThenTry(restrictedCall)
//                .onFailure((Throwable throwable) -> log.info("Wait before call it again :)"));
        return Try.of(supplier)
                .onFailure((Throwable throwable) -> {
                    log.info("Wait before call it again :)");
                })
                .get();
        //return "success";
        //return businessAService.success();
    }

    @GetMapping("success")
    @RateLimiter(name = "backendA")
    public String success(){
        return businessAService.success();
    }

    @GetMapping("successException")
    public String successException(){
        return businessAService.successException();
    }

    @GetMapping("ignore")
    public String ignore(){
        return businessAService.ignoreException();
    }

    @GetMapping("monoSuccess")
    public Mono<String> monoSuccess(){
        return businessAService.monoSuccess();
    }

    @GetMapping("monoFailure")
    public Mono<String> monoFailure(){
        return businessAService.monoFailure();
    }

    @GetMapping("fluxSuccess")
    public Flux<String> fluxSuccess(){
        return businessAService.fluxSuccess();
    }

    @GetMapping("monoTimeout")
    public Mono<String> monoTimeout(){
        return businessAService.monoTimeout();
    }

    @GetMapping("fluxTimeout")
    public Flux<String> fluxTimeout(){
        return businessAService.fluxTimeout();
    }

    @GetMapping("futureFailure")
    public CompletableFuture<String> futureFailure(){
        return businessAService.futureFailure();
    }

    @GetMapping("futureSuccess")
    public CompletableFuture<String> futureSuccess(){
        return businessAService.futureSuccess();
    }

    @GetMapping("futureTimeout")
    public CompletableFuture<String> futureTimeout(){
        return businessAService.futureTimeout();
    }

    @GetMapping("fluxFailure")
    public Flux<String> fluxFailure(){
        return businessAService.fluxFailure();
    }

    @GetMapping("fallback")
    public String failureWithFallback(){
        return businessAService.failureWithFallback();
    }

    @GetMapping("/process")
    public Mono<String> process() {
        return businessAService.process();
    }
}
