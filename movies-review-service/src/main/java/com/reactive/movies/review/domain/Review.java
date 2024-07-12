package com.reactive.movies.review.domain;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {
    @Id
    private String reviewId;
    @NotBlank
    private String movieInfoId;
    @Nullable
    private String comment;
    @NotNull
    @Positive(message = "Please pass a non-negative value for rating")
    @Min(value = 1, message = "Please pass a value for rating from 1 to 10")
    @Max(value = 10, message = "Please pass a value for rating from 1 to 10")
    private Double rating;
}
