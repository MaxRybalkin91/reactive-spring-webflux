package com.reactive.movies.exception;

public class MoviesReviewsClientException extends RuntimeException {
    private String message;

    public MoviesReviewsClientException(String message) {
        super(message);
        this.message = message;
    }
}
