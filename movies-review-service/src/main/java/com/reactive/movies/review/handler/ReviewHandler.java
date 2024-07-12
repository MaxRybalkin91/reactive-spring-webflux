package com.reactive.movies.review.handler;

import com.reactive.movies.review.domain.Review;
import com.reactive.movies.review.exception.ReviewDataException;
import com.reactive.movies.review.repository.ReviewRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewHandler {
    private final Validator validator;
    private final ReviewRepository reviewRepository;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        var movieInfoId = request.queryParam("movieInfoId");

        final Flux<Review> reviewsFlux;
        if (movieInfoId.isPresent()) {
            reviewsFlux = reviewRepository.findReviewsByMovieInfoId(movieInfoId.get());
        } else {
            reviewsFlux = reviewRepository.findAll();
        }
        return buildReviewsResponse(reviewsFlux);
    }

    private Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviewsFlux) {
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(req -> {
                    var reviewId = request.pathVariable("id");
                    var existingReview = reviewRepository.findById(reviewId);

                    return existingReview
                            .flatMap(review -> request.bodyToMono(Review.class)
                                    .map(reqReview -> {
                                        review.setComment(reqReview.getComment());
                                        review.setRating(reqReview.getRating());
                                        return review;
                                    })
                                    .flatMap(reviewRepository::save)
                                    .flatMap(savedReview -> ServerResponse.accepted().bodyValue(savedReview))
                            )
                            .switchIfEmpty(ServerResponse.noContent().build());
                });

    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        var existingReview = reviewRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> reviewRepository.deleteById(reviewId)
                        .then(Mono.defer(() -> ServerResponse.noContent().build())))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private void validate(Review review) {
        var constraints = validator.validate(review);
        if (!constraints.isEmpty()) {
            throw new ReviewDataException(constraints.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","))
            );
        }
    }
}
