package com.pharmacy.order.exception;

import feign.FeignException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> handleOrderNotFoundException(
            OrderNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DrugNotFoundException.class)
    public ResponseEntity<Object> handleDrugNotFoundException(
            DrugNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DrugUnavailableException.class)
    public ResponseEntity<Object> handleDrugUnavailableException(
            DrugUnavailableException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<Object> handleInvalidOrderException(
            InvalidOrderException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Object> handleServiceUnavailableException(
            ServiceUnavailableException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(
            FeignException ex, WebRequest request) {
        if (ex.status() == HttpStatus.NOT_FOUND.value()) {
            return buildErrorResponse(new DrugNotFoundException("Drug service returned not found"), 
                    HttpStatus.NOT_FOUND, request);
        } else if (ex.status() == HttpStatus.CONFLICT.value()) {
            return buildErrorResponse(new DrugUnavailableException("Drug not available in required quantity"), 
                    HttpStatus.CONFLICT, request);
        }
        return buildErrorResponse(new ServiceUnavailableException("Drug service unavailable"), 
                HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(
            Exception ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        body.put("errors", errors);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, headers, status);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception ex, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, status);
    }
}