package com.shorturl.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shorturl.entity.ShortUrl;
import com.shorturl.exception.ShortenedUrlNotFoundException;
import com.shorturl.exception.UninformedLabelException;
import com.shorturl.repository.ShortUrlRepository;
import com.shorturl.services.ShortUrlService;

@Controller
public class RedirectController {
	
	@Autowired
	public ShortUrlRepository repository;
	
	@Autowired
	private ShortUrlService service;
	
	@GetMapping("/{label}")
	public ResponseEntity<?> redirectToOriginalUrl(@PathVariable("label") String label) throws IOException, URISyntaxException{
		if(label == null) {
			throw new UninformedLabelException();
		}
		ShortUrl url = repository.findByShortUrlLabel(label);
		if(url == null) {
			throw new ShortenedUrlNotFoundException(label);
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(new URI(url.getOriginalUrl()));
		service.updateViews(url); //increment views
		return new ResponseEntity<String>(responseHeaders, HttpStatus.FOUND);
	}
	
}
