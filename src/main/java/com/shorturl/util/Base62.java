package com.shorturl.util;

public class Base62 {
	
	private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	
	public static String convertDecimalToBase62(long number) {
		if (number == 0) {
			return "0";
		}
		String encondedString = "";
		while (number != 0) {
			encondedString = CHARSET.charAt((int) (number % 62)) + encondedString;
			number /= 62;
		}
		return encondedString;
	}
	
	

}
