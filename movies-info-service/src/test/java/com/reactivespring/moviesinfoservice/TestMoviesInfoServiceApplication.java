package com.reactivespring.moviesinfoservice;

import org.springframework.boot.SpringApplication;

public class TestMoviesInfoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(MoviesInfoServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
