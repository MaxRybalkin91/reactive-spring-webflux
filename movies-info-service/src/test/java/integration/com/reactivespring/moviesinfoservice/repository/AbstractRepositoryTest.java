package com.reactivespring.moviesinfoservice.repository;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public abstract class AbstractRepositoryTest {
    protected static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setMongoDbProperties(DynamicPropertyRegistry registry) {
        if (!MONGO_DB_CONTAINER.isRunning()) {
            MONGO_DB_CONTAINER.start();
            registry.add("spring.data.mongodb.host", MONGO_DB_CONTAINER::getHost);
            registry.add("spring.data.mongodb.port", MONGO_DB_CONTAINER::getFirstMappedPort);
        }
    }
}
