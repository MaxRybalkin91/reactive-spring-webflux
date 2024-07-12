package com.reactive.movies.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    private final String message;

    public ReviewNotFoundException(String message, Throwable ex) {
        super(message, ex);
        this.message = message;
    }

    public ReviewNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
