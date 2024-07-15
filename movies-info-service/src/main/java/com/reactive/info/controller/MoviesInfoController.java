package com.reactive.info.controller;

import com.reactive.info.domain.MovieInfo;
import com.reactive.info.exception.MovieInfoNotFoundException;
import com.reactive.info.service.MoviesInfoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@Slf4j
public class MoviesInfoController {

    private MoviesInfoService moviesInfoService;

    public MoviesInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping("/movieInfos")
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year){
        log.info("Year is : {} ", year);
        if(year!=null){
            return moviesInfoService.getMovieInfoByYear(year);
        }
        return moviesInfoService.getAllMovieInfos().log();
    }

    /*@GetMapping("/movieInfos/{id}")
  public Mono<MovieInfo> getMovieInfoById(@PathVariable String id) {
      return moviesInfoService.getMovieInfoById(id);
  }
*/
    @GetMapping("/movieInfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id){
        return moviesInfoService.getMovieInfoById(id)
                .map(movieInfo1 -> ResponseEntity.ok()
                        .body(movieInfo1))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @PostMapping("/movieInfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        return moviesInfoService.addMovieInfo(movieInfo).log();

    }

    @PutMapping("/movieInfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo updatedMovieInfo, @PathVariable String id){
        return moviesInfoService.updateMovieInfo(updatedMovieInfo, id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.error(new MovieInfoNotFoundException("MovieInfo Not Found")))
                //.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                //.switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).<MovieInfo>body("MovieInfo Not Found")))
                .log();

    }

    @DeleteMapping("/movieInfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id){
        return moviesInfoService.deleteMovieInfo(id);

    }
}
