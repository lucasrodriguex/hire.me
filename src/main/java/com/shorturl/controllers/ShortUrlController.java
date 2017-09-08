package com.shorturl.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shorturl.entity.ShortUrl;
import com.shorturl.repository.ShortUrlRepository;
import com.shorturl.services.ShortUrlService;
import com.shorurl.response.ShortUrlSuccessResponse;

@RestController
@RequestMapping("/shorturl")
public class ShortUrlController {
	
	@Autowired
	private ShortUrlService service;
	
	@Autowired
	private ShortUrlRepository repository;
	
	@PostMapping
	public ResponseEntity<?> insert(@RequestParam final String url, 
								   @RequestParam(value="custom_label", required=false) final String customLabel) throws MalformedURLException {
		final long initialTime = System.currentTimeMillis();
		
		new URL(url);
		service.saveUrl(customLabel, url);
		
		final long totalTime = System.currentTimeMillis() - initialTime;
		
		HashMap<String, String> statistics = new HashMap<String, String>();
		statistics.put("time_taken", totalTime + " ms");
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new ShortUrlSuccessResponse(service.getLabel(), url, statistics));
	}
	
	@GetMapping
	public ResponseEntity<?> read(@RequestParam String label) {
		ShortUrl shortUrl = service.getShortUrlByLabel(label);
		return ResponseEntity.ok().body(shortUrl);
	}
	
	@GetMapping("/topTenViews")
	public ResponseEntity<?> getTopTenViews() {
		List<ShortUrl> shorturl = repository.findTop10ByOrderByViewsDesc();
		return ResponseEntity.ok().body(shorturl);		
	}
	
}
