package com.reactive.review;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class TestContainersConfig {
    @Container
    @ServiceConnection
    public static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    static {
        MONGO_DB_CONTAINER.start();
    }
}
