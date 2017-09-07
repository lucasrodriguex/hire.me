package com.shorturl.controllers;

import java.net.MalformedURLException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shorturl.exception.LabelAlreadyExistsException;
import com.shorturl.exception.ShortenedUrlNotFoundException;
import com.shorturl.exception.UninformedLabelException;
import com.shorturl.infra.ErrorCodes;
import com.shorurl.response.ShortUrlErrorResponse;

@ControllerAdvice
@RestController
public class GlobalExceptionHandlerController {
	
	@ExceptionHandler(LabelAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ShortUrlErrorResponse handle(LabelAlreadyExistsException e) {
		final String label = e.getMessage() != null ? e.getMessage() : "";
        return new ShortUrlErrorResponse(label,
				ErrorCodes.CUSTOM_LABEL_ALREADY_EXISTS_ERROR_CODE, 
				ErrorCodes.CUSTOM_LABEL_ALREADY_EXISTS_ERROR_DESCRIPTION);
    }
	
	@ExceptionHandler(MalformedURLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ShortUrlErrorResponse handle(MalformedURLException e) {
        return new ShortUrlErrorResponse("",
				ErrorCodes.INVALID_URL_FORMAT_ERROR_CODE, 
				ErrorCodes.INVALID_URL_FORMAT_ERROR_DESCRIPTION);
    }
	
	@ExceptionHandler(ShortenedUrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ShortUrlErrorResponse handle(RuntimeException e) {
		final String label = e.getMessage() != null ? e.getMessage() : "";
        return new ShortUrlErrorResponse(label,
    			ErrorCodes.SHORTENED_URL_NOT_FOUND_ERROR_CODE, 
    			ErrorCodes.SHORTENED_URL_NOT_FOUND_ERROR_DESCRIPTION);
    }
	
	@ExceptionHandler(UninformedLabelException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ShortUrlErrorResponse handle(UninformedLabelException e) {
		final String label = e.getMessage() != null ? e.getMessage() : "";
        return new ShortUrlErrorResponse(label,
				ErrorCodes.UNINFORMED_LABEL_ERROR_CODE, 
				ErrorCodes.UNINFORMED_LABEL_ERROR_DESCRIPTION);
    }

}
