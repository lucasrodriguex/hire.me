package com.shorturl.exception;

public class UninformedLabelException extends IllegalArgumentException {
	
	private static final long serialVersionUID = 1L;

	public UninformedLabelException(String message) {
        super(message);
    }
	
	public UninformedLabelException() {
        this(null);
    }

}
