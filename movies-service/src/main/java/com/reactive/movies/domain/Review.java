package com.reactive.movies.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String reviewId;
    private String movieInfoId;
    private String comment;
    private Double rating;
}
