package com.pharmacy.order.exception;

	public class DrugNotFoundException extends RuntimeException {
	    public DrugNotFoundException(String message) {
	        super(message);
	    }
	}
