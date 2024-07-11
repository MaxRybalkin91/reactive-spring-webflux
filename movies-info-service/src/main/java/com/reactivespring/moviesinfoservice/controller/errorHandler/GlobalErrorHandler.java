package com.reactivespring.moviesinfoservice.controller.errorHandler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRequestBodyError(WebExchangeBindException ex) {
        var error = ex.getBindingResult().getAllErrors().stream()
                .map(objectError -> {
                    if (objectError != null
                            && objectError.getDefaultMessage() != null) {
                        return objectError.getDefaultMessage();
                    } else {
                        return "Unknown error";
                    }
                })
                .sorted()
                .collect(Collectors.joining(","));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRequestParamError(ConstraintViolationException ex) {
        var errors = ex.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage())
                .collect(Collectors.joining(","));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
