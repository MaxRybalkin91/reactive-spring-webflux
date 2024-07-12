package com.reactive.movies.review.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {
    @GetMapping("flux")
    public Flux<Integer> getFlux() {
        return Flux.just(1, 2, 3).log();
    }

    @GetMapping("mono")
    public Mono<String> getMono() {
        return Mono.just("MONO").log();
    }

    @GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> getStream() {
        return Flux
                .interval(Duration.ofMillis(500))
                .log();
    }
}
