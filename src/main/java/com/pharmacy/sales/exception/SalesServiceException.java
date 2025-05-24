
package com.pharmacy.sales.exception;

public class SalesServiceException extends RuntimeException {
    public SalesServiceException(String message) {
        super(message);
    }

    public SalesServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
