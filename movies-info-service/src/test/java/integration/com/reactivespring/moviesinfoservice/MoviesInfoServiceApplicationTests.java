package com.reactivespring.moviesinfoservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = MoviesInfoServiceApplication.class)
class MoviesInfoServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
