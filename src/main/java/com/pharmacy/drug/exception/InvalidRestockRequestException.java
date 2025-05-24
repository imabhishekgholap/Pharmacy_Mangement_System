package com.pharmacy.drug.exception;

public class InvalidRestockRequestException extends RuntimeException{

	public InvalidRestockRequestException(String message){
		super(message);
	}
}