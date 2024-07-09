package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {
    @Autowired
    private MovieInfoRepository movieInfoRepository;

    private static final List<MovieInfo> MOVIE_INFOS = List.of(
            new MovieInfo(null, "The Terminator", 1986,
                    List.of("Arnold Schwarzenegger", "Linda Hamilton", "Michael Bean"), LocalDate.of(1984, 10, 26)),
            new MovieInfo(null, "The Matrix", 1999,
                    List.of("Keanu Reeves", "Carrie-Anne Moss", "Laurence Fishburn", "Hugo Weaving"),
                    LocalDate.of(1999, 3, 31))
    );

    @BeforeEach
    public void setUp() {
        movieInfoRepository.saveAll(MOVIE_INFOS).blockLast();
    }

    @AfterEach
    public void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void checkAllMovies() {
        var allMovies = movieInfoRepository.findAll();

        StepVerifier.create(allMovies)
                .expectNextCount(MOVIE_INFOS.size())
                .verifyComplete();
    }

}
