package com.reactive.movies.client;

import com.reactive.movies.domain.Review;
import com.reactive.movies.exception.MoviesReviewsClientException;
import com.reactive.movies.exception.MoviesReviewsServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MoviesReviewsRestClient {
    private static final String MOVIE_INFO_ID_PARAMETER = "movieInfoId";

    private final WebClient webClient;

    @Value("${webClient.moviesReview.url}")
    private String moviesReviewUrl;

    public Flux<Review> retrieveReviews(String movieId) {
        var url = UriComponentsBuilder.fromHttpUrl(moviesReviewUrl)
                .queryParam(MOVIE_INFO_ID_PARAMETER, movieId)
                .buildAndExpand().toUriString();

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, get4xxErrorResult(movieId))
                .onStatus(HttpStatusCode::is5xxServerError, get5xxErrorResult(movieId)
                )
                .bodyToFlux(Review.class);
    }

    private static Function<ClientResponse, Mono<? extends Throwable>> get4xxErrorResult(String movieId) {
        return clientResponse -> {
            if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                return Mono.error(new MoviesReviewsClientException("No reviews were found for " + movieId));
            }
            return clientResponse.bodyToMono(String.class)
                    .flatMap(message -> Mono.error(new MoviesReviewsClientException("Error requesting reviews for " + movieId)));
        };
    }

    private static Function<ClientResponse, Mono<? extends Throwable>> get5xxErrorResult(String movieId) {
        return clientResponse ->
                clientResponse.bodyToMono(String.class)
                        .flatMap(message ->
                                Mono.error(new MoviesReviewsServerException("Error connecting reviews server for " + movieId)));
    }
}
