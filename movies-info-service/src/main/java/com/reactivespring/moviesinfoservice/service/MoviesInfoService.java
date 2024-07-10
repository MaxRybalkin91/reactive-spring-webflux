package com.reactivespring.moviesinfoservice.service;


import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MoviesInfoService {
    private final MovieInfoRepository movieInfoRepository;

    @Transactional
    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    @Transactional
    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, @NotBlank String id) {
        return movieInfoRepository.findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.setCast(updatedMovieInfo.getCast());
                    movieInfo.setName(updatedMovieInfo.getName());
                    movieInfo.setReleaseDate(updatedMovieInfo.getReleaseDate());
                    movieInfo.setYear(updatedMovieInfo.getYear());
                    return movieInfoRepository.save(movieInfo);
                });
    }

    @Transactional
    public Mono<Void> deleteMovieInfoById(String id) {
        return movieInfoRepository.deleteById(id);
    }
}
