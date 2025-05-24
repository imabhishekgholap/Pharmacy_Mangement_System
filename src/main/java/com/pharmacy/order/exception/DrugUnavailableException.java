package com.pharmacy.order.exception;

public class DrugUnavailableException extends RuntimeException {
    public DrugUnavailableException(String message) {
        super(message);
    }
}
