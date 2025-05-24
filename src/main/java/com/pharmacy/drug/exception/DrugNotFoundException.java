/**
 * Custom exception thrown when a requested drug is not found in the system.
 */
package com.pharmacy.drug.exception;

public class DrugNotFoundException extends RuntimeException{

	public DrugNotFoundException(String message){
		super(message);
	}
}
