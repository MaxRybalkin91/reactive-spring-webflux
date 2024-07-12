package routes;

import com.reactive.movies.review.domain.Review;
import com.reactive.movies.review.exception.handler.GlobalErrorHandler;
import com.reactive.movies.review.handler.ReviewHandler;
import com.reactive.movies.review.repository.ReviewRepository;
import com.reactive.movies.review.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {
    private static final String REVIEWS_URL = "/v1/reviews";

    @MockBean
    private ReviewRepository reviewRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview_success() {
        var review = new Review(null, "abc1", "Awesome Movie", 9.0);

        when(reviewRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", "abc1", "Awesome Movie", 9.0)));

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
    void getAllReviews_success() {
        when(reviewRepository.findAll())
                .thenReturn(Flux.just(new Review("abc", "abc1", "Awesome Movie", 9.0)));

        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(1);
    }

    @Test
    void updateReviewById_success() {
        var review = new Review("abc", "abc1", "Awesome Movie", 9.0);
        var updatedReview = new Review("abc", "abc1", "Awful Movie", 2.0);

        when(reviewRepository.findById(eq(review.getReviewId()))).thenReturn(Mono.just(review));

        when(reviewRepository.save(argThat(it ->
                it.getReviewId().equals(review.getReviewId())
                        && it.getComment() != null
                        && it.getComment().equals(review.getComment())
        ))).thenReturn(Mono.just(updatedReview));

        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", review.getReviewId())
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isAccepted()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedReview = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                });
    }

    @Test
    void updateReviewById_noContent() {
        var review = new Review("abc", "abc1", "Awesome Movie", 9.0);
        when(reviewRepository.findById(eq(review.getReviewId()))).thenReturn(Mono.empty());

        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", review.getReviewId())
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void deleteReviewById_success() {
        var review = new Review("abc", "abc1", "Awful Movie", 2.0);
        when(reviewRepository.findById(eq(review.getReviewId()))).thenReturn(Mono.just(review));
        when(reviewRepository.deleteById(eq(review.getReviewId()))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/{id}", review.getReviewId())
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
    }

    @Test
    void deleteReviewById_notFound() {
        var review = new Review("abc", "abc1", "Awful Movie", 2.0);
        when(reviewRepository.findById(eq(review.getReviewId()))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/{id}", review.getReviewId())
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
