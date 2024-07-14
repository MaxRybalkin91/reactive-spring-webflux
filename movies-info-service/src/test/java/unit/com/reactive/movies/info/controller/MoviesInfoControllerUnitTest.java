package com.reactive.movies.info.controller;

import com.reactive.movies.info.domain.MovieInfo;
import com.reactive.movies.info.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {
    private static final String MOVIES_INFO_URL = "/v1/moviesInfo";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoServiceMock;

    @Test
    void getAllMoviesInfo_success() {
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(moviesInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMoviesInfoById_success() {
        var movieInfo = new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        when(moviesInfoServiceMock.getMovieInfoById(eq(movieInfo.getMovieInfoId())))
                .thenReturn(Mono.just(movieInfo));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfo.getMovieInfoId())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo(movieInfo.getName())
                .jsonPath("$.year").isEqualTo(movieInfo.getYear())
                .jsonPath("$.cast[0]").isEqualTo(movieInfo.getCast().get(0))
                .jsonPath("$.cast[1]").isEqualTo(movieInfo.getCast().get(1));
    }

    @Test
    void addMovieInfo_success() {
        var movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(
                Mono.just(new MovieInfo("mockId", "Batman Begins1",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
        );

        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() != null;
                    assertEquals("mockId", savedMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void updateMovieInfoById_success() {
        var movieInfoId = "abc";
        var movieInfo = new MovieInfo(null, "Dark Knight Rises1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(moviesInfoServiceMock.updateMovieInfoById(isA(MovieInfo.class), isA(String.class))).thenReturn(
                Mono.just(new MovieInfo(movieInfoId, "Dark Knight Rises1",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
        );

        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedMovieInfo != null;
                    assert updatedMovieInfo.getMovieInfoId() != null;
                    assertEquals("Dark Knight Rises1", updatedMovieInfo.getName());
                });
    }

    @Test
    void deleteMovieInfoById_success() {
        var movieInfoId = "abc";

        when(moviesInfoServiceMock.deleteMovieInfoById(eq(movieInfoId))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
    }

    @Test
    void deleteMovieInfoByNullableId_success() {
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", " ")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void findMovieInfoByNullableId_badRequest() {
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", " ")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void updateMovieInfoByNullableId_badRequest() {
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", " ")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
