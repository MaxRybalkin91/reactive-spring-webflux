package com.reactive.movies.exception;

public class MoviesReviewsServerException extends RuntimeException{
    private String message;

    public MoviesReviewsServerException(String message) {
        super(message);
        this.message = message;
    }
}
