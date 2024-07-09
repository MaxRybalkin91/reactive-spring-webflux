package com.reactivespring.domain;

import jakarta.validation.constraints.PositiveOrZero;
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
    private Long movieInfoId;
    private String comment;
    @PositiveOrZero(message = "rating.negative : please pass a non-negative value")
    private Double rating;
}
