package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static com.learnreactiveprogramming.service.FluxAndMonoGeneratorService.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FluxAndMonoGeneratorServiceTest {
    private final FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux_list() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        var publisher = StepVerifier.create(namesFlux);

        publisher
                .expectNext(NAMES.toArray(new String[0]))
                .verifyComplete();
    }

    @Test
    void namesFlux_count() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        var publisher = StepVerifier.create(namesFlux);

        publisher
                .expectNextCount(NAMES.size())
                .verifyComplete();
    }

    @Test
    void namesFlux_uppercase() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxUpperCase();
        var publisher = StepVerifier.create(namesFlux);

        publisher
                .expectNext(NAMES_UPPERCASE.toArray(new String[0]))
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap();
        var publisher = StepVerifier.create(namesFlux);

        publisher
                .recordWith(ArrayList::new)
                .thenConsumeWhile(it -> true)
                .consumeRecordedWith(values -> assertTrue(values.size() > NAMES.size()))
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap_no_order() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapDelay();
        var publisher = StepVerifier.create(namesFlux);

        Assertions.assertThrows(AssertionError.class, () ->
                publisher
                        .expectNext(NAMES_UPPERCASE_LETTERS.toArray(new String[0]))
                        .verifyComplete());
    }

    @Test
    void namesFlux_concatMap_keep_order() {
        var namesFlux = fluxAndMonoGeneratorService.namesConcatMapDelay();
        var publisher = StepVerifier.create(namesFlux);

        publisher
                .expectNext(NAMES_UPPERCASE_LETTERS.toArray(new String[0]))
                .verifyComplete();
    }
}