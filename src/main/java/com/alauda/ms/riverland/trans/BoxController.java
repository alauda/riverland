package com.alauda.ms.riverland.trans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/box")
public class BoxController {

    private final static Logger log = LoggerFactory.getLogger(BoxController.class);
    @Autowired
    BoxService service;

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

    @GetMapping("/circuit")
    public String circuit() {
        return service.circuit();
    }

    @GetMapping("/success")
    public String success() {
        return service.success();
    }

    @GetMapping(value = "/tweets-non-blocking",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getTweetsNonBlocking() {
        log.info("Starting NON-BLOCKING Controller!");
        Flux<String> tweetFlux = Flux.just("Hands", "Feet", "Arms", "Legs");

        //tweetFlux.subscribe(tweet -> log.info(tweet));
        log.info("Exiting NON-BLOCKING Controller!");
        return tweetFlux;
    }

    @GetMapping("/process10")
    public Mono<String> process10() {
        return service.processLess();
    }

    @GetMapping("/process20")
    public Mono<String> process20() {
        return service.process();
    }

    @GetMapping("/breaker/success")
    public Mono<String> breakerSuccess() {
        return service.breakerSuccess();
    }

    @GetMapping("/breaker/down")
    public String breakerDown() {
        return service.breakerFailure();
    }

    @GetMapping("/breaker/failure")
    public String breaker() {
        return service.failure();
    }
}
