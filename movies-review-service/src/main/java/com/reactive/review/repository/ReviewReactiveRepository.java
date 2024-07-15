package com.reactive.review.repository;

import com.reactive.review.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findReviewsByMovieInfoId(Long movieInfoId);
}
