package com.maybank.transaction.exception;

public class ConcurrentUpdateException extends RuntimeException {

	public ConcurrentUpdateException(String message) {
		super(message);
	}

}
