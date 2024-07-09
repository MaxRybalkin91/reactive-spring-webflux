package com.learnreactiveprogramming.service;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public static final List<String> NAMES = List.of("Alex", "John", "Jimmie");
    public static final List<String> NAMES_UPPERCASE = NAMES.stream().map(String::toUpperCase).toList();
    public static final List<String> NAMES_UPPERCASE_LETTERS = NAMES_UPPERCASE.stream().map(s -> s.split(""))
            .flatMap(Arrays::stream)
            .toList();

    public static void main(String[] args) {
        new FluxAndMonoGeneratorService().namesFlux().subscribe(System.out::println);
        new FluxAndMonoGeneratorService().nameMono().subscribe(System.out::println);
        new FluxAndMonoGeneratorService().namesFluxFlatMap().subscribe(System.out::println);
        new FluxAndMonoGeneratorService().namesFluxFlatMapDelay().subscribe(System.out::println);
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(NAMES) //db or a remote service call
                .log(); //shows each step of calling the publisher
    }

    public Flux<String> namesFluxUpperCase() {
        return Flux.fromIterable(NAMES) //db or a remote service call
                .map(String::toUpperCase) //transfers every part to upperCase
                .log(); //shows each step of calling the publisher
    }

    public Mono<String> nameMono() {
        return Mono.just("Alex") //db or a remote service call
                .log(); //shows each step of calling the publisher
    }

    public Flux<String> namesFluxFlatMap() {
        return Flux.fromIterable(NAMES) //db or a remote service call
                .flatMap(name -> Flux.fromArray(name.split(""))) //splits into letters
                .log(); //shows each step of calling the publisher
    }

    public Flux<String> namesFluxFlatMapDelay() {
        final Random random = new Random();
        return Flux.fromIterable(NAMES_UPPERCASE) //db or a remote service call
                .flatMap(getStringSplit(random))
                .log(); //shows each step of calling the publisher
    }

    public Flux<String> namesConcatMapDelay() {
        final Random random = new Random();
        return Flux.fromIterable(NAMES_UPPERCASE) //db or a remote service call
                .concatMap(getStringSplit(random))
                .log(); //shows each step of calling the publisher
    }

    public Flux<String> namesFlatMapMany() {
        return Mono.just(NAMES_UPPERCASE_LETTERS) //db or a remote service call
                .flatMapMany(Flux::just)
                .map(it -> it.get(0))
                .log(); //shows each step of calling the publisher
    }

    private Function<String, Publisher<? extends String>> getStringSplit(Random random) {
        return name -> Flux.fromArray(name.split("")) //splits into letters
                .delayElements(Duration.ofMillis(random.nextInt(100)));
    }

    private Function<List<String>, Publisher<? extends String>> getStringSplit() {
        return Flux::fromIterable;
    }
}
