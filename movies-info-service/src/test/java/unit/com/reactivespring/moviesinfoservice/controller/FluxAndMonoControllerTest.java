package com.reactivespring.moviesinfoservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = FluxAndMonoController.class)
@RunWith(SpringRunner.class)
public class FluxAndMonoControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void checkFlux() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Integer.class)
                .hasSize(3);
    }

    @Test
    public void checkFluxBodyWithVerifier() {
        var response = webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(response)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    public void checkFluxBodyWithConsumer() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Integer.class)
                .consumeWith(result -> {
                    var response = result.getResponseBody();
                    assert (response != null);
                    assert (response.size() == 3);
                });
    }

    @Test
    public void checkMono() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class);
    }

    @Test
    public void checkMonoBodyWithVerifier() {
        var response = webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(response)
                .expectNext("MONO")
                .verifyComplete();
    }

    @Test
    public void checkMonoBodyWithConsumer() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    var response = result.getResponseBody();
                    assert ("MONO".equals(response));
                });
    }

    @Test
    public void checkStreamBodyWithVerifier() {
        var response = webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(response)
                .expectNext(0, 1, 2, 3)
                .thenCancel()
                .verify();
    }
}
