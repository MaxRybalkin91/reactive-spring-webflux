package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MoviesInfoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/moviesInfo")
@RequiredArgsConstructor
@Validated
@Slf4j
public class MoviesInfoController {
    private final MoviesInfoService moviesInfoService;

    @GetMapping
    public Flux<ResponseEntity<MovieInfo>> getAllMovieInfos(@RequestParam("year") Integer year) {
        Flux<MovieInfo> response;
        if (year == null) {
            response = moviesInfoService.getAllMovieInfos();
        } else {
            response = moviesInfoService.getMovieInfoByYear(year);
        }
        return response
                .map(movieInfo -> ResponseEntity.ok().body(movieInfo))
                .switchIfEmpty(Flux.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable("id") @NotBlank String id) {
        return moviesInfoService.getMovieInfoById(id)
                .map(movieInfo -> ResponseEntity.ok().body(movieInfo))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<MovieInfo>> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body))
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable("id") @NotBlank String id, @RequestBody @Valid MovieInfo updatedMovieInfo) {
        return moviesInfoService.updateMovieInfoById(updatedMovieInfo, id)
                .map(body -> ResponseEntity.status(HttpStatus.ACCEPTED).body(body))
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteMovieInfoById(@PathVariable("id") @NotBlank String id) {
        return moviesInfoService.deleteMovieInfoById(id)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }
}
