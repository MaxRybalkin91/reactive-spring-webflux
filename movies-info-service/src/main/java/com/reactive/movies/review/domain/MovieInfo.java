package com.reactive.movies.review.domain;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {
    public static final String NAME_MESSAGE = "Name must be not blank";
    public static final String YEAR_MESSAGE = "Year must be not null, and must have a positive value";
    public static final String CAST_MESSAGE = "Cast list must be filled";
    public static final String EACH_CAST_MESSAGE = "Each cast value must be not blank";
    public static final String RELEASE_DATE_MESSAGE = "Release date must be not null, and must match pattern YYYY-MM-DD";

    @Id
    @Nullable
    private String movieInfoId;
    @NotBlank(message = NAME_MESSAGE)
    private String name;
    @NotNull(message = YEAR_MESSAGE)
    @Positive(message = YEAR_MESSAGE)
    private Integer year;
    @NotNull(message = CAST_MESSAGE)
    private List<@NotBlank(message = EACH_CAST_MESSAGE) String> cast;
    @NotNull(message = RELEASE_DATE_MESSAGE)
    private LocalDate releaseDate;
}
