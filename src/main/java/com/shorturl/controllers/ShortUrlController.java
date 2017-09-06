package com.shorturl.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shorturl.entity.ShortUrl;
import com.shorturl.repository.ShortUrlRepository;
import com.shorturl.services.ShortUrlService;

@RestController
public class ShortUrlController {
	
	@Autowired
	private ShortUrlService shortUrlService;
	
	@Autowired
	private ShortUrlRepository shortUrlRepository;
	
	@RequestMapping("/")
	public String index() {
		return "Ok";
	}
	
	
	@RequestMapping(value = "/shorturl", method = RequestMethod.POST)
	public ResponseEntity<?> insert(@RequestParam String url, 
								   @RequestParam(value="custom_label", required=false) String customLabel) {
		final long initialTime = System.currentTimeMillis();
		try {
			new URL(url);
			shortUrlService.save(customLabel, url);
		} catch (MalformedURLException e) {
			return ResponseEntity.badRequest().body("Deu ruim");
		} catch (IllegalArgumentException e){
			return ResponseEntity.badRequest().body("Deu ruim");
		}
		
		final long totalTime = System.currentTimeMillis() - initialTime;

		return ResponseEntity.status(HttpStatus.CREATED).body("Criado com sucesso! em "+ totalTime + " ms");
	}
	
	@RequestMapping(value = "/shorturl", method = RequestMethod.GET)
	public ResponseEntity<?> read(@RequestParam String label) {
		if (label == null) {
			return ResponseEntity.badRequest().body("URL NAO INFORMADA");
		}
		ShortUrl shortUrl = shortUrlRepository.findByShortUrlLabel(label);
		if(shortUrl == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Url nao encontrada");
		}
		
		shortUrlService.updateViews(shortUrl);
		return ResponseEntity.ok().body("Sucesso!");
		
	}
	
	@RequestMapping(value = "/shorturl/findAll", method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		List<ShortUrl> shorturl = (List<ShortUrl>) shortUrlRepository.findAll();
		return ResponseEntity.ok().body(shorturl);		
	}
	
	
}
