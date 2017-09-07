package com.shorurl.response;

public class ShortUrlErrorResponse {

	public String label;
	public String error_code;
	public String description;
	
	public ShortUrlErrorResponse(String label, String error_code, String description) {
		this.label = label;
		this.error_code = error_code;
		this.description = description;
	}
	
}
