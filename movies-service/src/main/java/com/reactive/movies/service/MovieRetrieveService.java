package com.reactive.movies.service;

import com.reactive.movies.client.MoviesInfoRestClient;
import com.reactive.movies.client.MoviesReviewsRestClient;
import com.reactive.movies.domain.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieRetrieveService {
    private final MoviesInfoRestClient infoRestClient;
    private final MoviesReviewsRestClient reviewsRestClient;

    public Mono<Movie> retrieveMovieById(String movieId) {
        return infoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var reviewsList = reviewsRestClient.retrieveReviews(movieInfo.getMovieInfoId()).collectList();
                    return reviewsList.map(reviews -> new Movie(movieInfo, reviews));
                });
    }
}
