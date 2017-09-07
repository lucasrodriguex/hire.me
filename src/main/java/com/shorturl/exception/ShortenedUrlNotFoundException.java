package com.shorturl.exception;

public class ShortenedUrlNotFoundException extends IllegalArgumentException {
	
	private static final long serialVersionUID = 1L;

	public ShortenedUrlNotFoundException(String message) {
        super(message);
    }
	
	public ShortenedUrlNotFoundException() {
        this(null);
    }

}
