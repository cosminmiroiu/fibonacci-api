package com.fibapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FibonacciExceptionHandler {

    @ExceptionHandler(FibonacciRuntimeException.class)
    public ResponseEntity<String> handleFibonacciException(FibonacciRuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
