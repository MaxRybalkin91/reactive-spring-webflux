package com.reactive.movies.controller;

import com.reactive.movies.domain.Movie;
import com.reactive.movies.service.MovieRetrieveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MoviesController {
    private final MovieRetrieveService movieRetrieveService;

    @GetMapping("/movie/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
        return movieRetrieveService.retrieveMovieById(movieId);
    }
}
