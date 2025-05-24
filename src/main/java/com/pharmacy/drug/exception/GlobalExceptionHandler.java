package com.pharmacy.drug.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", "Validation failed", request, validationErrors);
    }

    @ExceptionHandler(InvalidSupplierException.class)
    public ResponseEntity<Object> handleInvalidSupplierException(InvalidSupplierException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Supplier Error", ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidRestockRequestException.class)
    public ResponseEntity<Object> handleInvalidRestockRequestException(InvalidRestockRequestException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Restock Request Error", ex.getMessage(), request, null);
    }

    @ExceptionHandler(DrugNotFoundException.class)
    public ResponseEntity<Object> handleDrugNotFoundException(DrugNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Drug Not Found", ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request, null);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String error, String message, WebRequest request, Map<String, ?> details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        if (details != null && !details.isEmpty()) {
            body.put("details", details);
        }
        return new ResponseEntity<>(body, status);
    }
}
