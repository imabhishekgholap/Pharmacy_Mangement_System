// ServiceUnavailableException.java
package com.pharmacy.order.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}