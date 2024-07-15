package com.reactive.config;

import com.reactive.exception.MoviesInfoServerException;
import com.reactive.exception.ReviewsServerException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

@Configuration
public class RetryConfig {
    @Bean
    public Retry retry() {
        return Retry.max(3)
                .filter(ex -> ex instanceof MoviesInfoServerException ||
                        ex instanceof ReviewsServerException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure())));
    }
}
