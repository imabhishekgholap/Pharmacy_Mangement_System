package com.pharmacy.security.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mongodb.DuplicateKeyException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateKey(DuplicateKeyException e) {
        return ResponseEntity.badRequest().body("Username already exists");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
