package com.reactive.info.controller;

import com.reactive.info.domain.MovieInfo;
import com.reactive.info.exception.MovieInfoNotFoundException;
import com.reactive.info.service.MoviesInfoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping(path = "/v1")
@Slf4j
public class MoviesInfoController {
    private final MoviesInfoService moviesInfoService;

    private static final Sinks.Many<MovieInfo> GET_SINK = Sinks.many().multicast().onBackpressureBuffer();
    private static final Sinks.Many<MovieInfo> DELETE_SINK = Sinks.many().multicast().onBackpressureBuffer();
    private static final Sinks.Many<MovieInfo> UPDATE_SINK = Sinks.many().multicast().onBackpressureBuffer();

    public MoviesInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
        moviesInfoService.getAllMovieInfos().toStream().forEach(GET_SINK::tryEmitNext);

        final Flux<MovieInfo> filteredStream = GET_SINK.asFlux()
                .filter(data -> DELETE_SINK.asFlux().toStream().noneMatch(data::equals))
                .flatMap(data -> UPDATE_SINK.asFlux().reduce(data, (previous, updated) -> updated));

        filteredStream.subscribe(GET_SINK::tryEmitNext);
    }

    @GetMapping(path = "/movieInfos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getAllMovieInfos() {
        return GET_SINK.asFlux();
    }

    @GetMapping(path = "/movieInfos/{id}")
    public Mono<MovieInfo> getMovieInfoById(@PathVariable("id") String id) {
        return moviesInfoService.getMovieInfoById(id)
                .switchIfEmpty(Mono.error(new MovieInfoNotFoundException("MovieInfo Not Found")));
    }

    @PostMapping(path = "/movieInfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo)
                .doOnNext(GET_SINK::tryEmitNext);
    }

    @PutMapping(path = "/movieInfos/{id}")
    public Mono<MovieInfo> updateMovieInfo(@RequestBody MovieInfo updatedMovieInfo,
                                           @PathVariable("id") String id) {
        return moviesInfoService.updateMovieInfo(updatedMovieInfo, id)
                .doOnNext(UPDATE_SINK::tryEmitNext)
                .switchIfEmpty(Mono.error(new MovieInfoNotFoundException("MovieInfo Not Found")));
    }

    @DeleteMapping(path = "/movieInfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<MovieInfo> deleteMovieInfo(@PathVariable("id") String id) {
        return moviesInfoService.deleteMovieInfo(id)
                .doOnNext(DELETE_SINK::tryEmitNext);
    }
}
