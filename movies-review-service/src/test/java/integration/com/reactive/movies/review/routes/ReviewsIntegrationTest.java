package com.reactive.movies.review.routes;

import com.reactive.movies.review.TestContainersConfig;
import com.reactive.movies.review.domain.Review;
import com.reactive.movies.review.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntegrationTest extends TestContainersConfig {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ReviewRepository reviewRepository;

    private static final String REVIEWS_URL = "/v1/reviews";

    private static final List<Review> REVIEWS = List.of(
            new Review(null, "abc1", "Awesome Movie", 9.0),
            new Review(null, "abc2", "Awesome Movie1", 9.0),
            new Review(null, "abc3", "Excellent Movie", 8.0));

    @BeforeEach
    void setUp() {
        reviewRepository.saveAll(REVIEWS).blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll().block();
    }

    @Test
    @DirtiesContext
    void addReview_success() {
        var review = new Review(null, "abc1", "Awesome Movie", 9.0);

        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedReview = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                });
    }

    @Test
    @DirtiesContext
    void getAllReviews_success() {
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(REVIEWS.size());
    }

    @Test
    @DirtiesContext
    void updateReview_success() {
        var review = REVIEWS.get(0);
        review.setRating(5.);

        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", review.getReviewId())
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isAccepted()
                .expectBody(Review.class)
                .consumeWith(movieReviewEntityExchangeResult -> {
                    var updatedMovieReview = movieReviewEntityExchangeResult.getResponseBody();
                    assert updatedMovieReview != null;
                    assert updatedMovieReview.getRating() != null;
                    assertEquals(review.getRating(), updatedMovieReview.getRating());
                });
    }
}
