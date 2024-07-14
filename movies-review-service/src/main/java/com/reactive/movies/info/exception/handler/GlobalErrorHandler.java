package com.reactive.movies.info.exception.handler;

import com.reactive.movies.info.exception.ReviewDataException;
import com.reactive.movies.info.exception.ReviewNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Exception message is {} ", ex.getMessage(), ex);
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        var errorMessage = dataBufferFactory.wrap(ex.getMessage().getBytes());
        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof ReviewDataException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ReviewNotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().writeWith(Mono.just(errorMessage));
    }
}
