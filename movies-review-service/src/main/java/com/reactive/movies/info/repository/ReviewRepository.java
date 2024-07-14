package com.reactive.movies.info.repository;

import com.reactive.movies.info.domain.Review;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReviewRepository extends ReactiveCrudRepository<Review, String> {
    Flux<Review> findReviewsByMovieInfoId(String id);
}
