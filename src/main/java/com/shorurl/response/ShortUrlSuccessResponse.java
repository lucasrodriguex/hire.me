package com.shorurl.response;

import java.util.HashMap;

public class ShortUrlSuccessResponse {
	public String label;
	public String originalUrl;
	public HashMap<String, String> statistics;
	
	public ShortUrlSuccessResponse(String label, String originalUrl, HashMap<String,String> statistics) {
		this.label = label;
		this.originalUrl = originalUrl;
		this.statistics = statistics;
	}
}
