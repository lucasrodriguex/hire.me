package com.shorturl.exception;

public class LabelAlreadyExistsException extends IllegalArgumentException {
	
	private static final long serialVersionUID = 1L;

	public LabelAlreadyExistsException(String message) {
        super(message);
    }
	
	public LabelAlreadyExistsException() {
        this(null);
    }

}
