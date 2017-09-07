package com.shorturl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shorturl.entity.ShortUrl;
import com.shorturl.exception.LabelAlreadyExistsException;
import com.shorturl.exception.ShortenedUrlNotFoundException;
import com.shorturl.exception.UninformedLabelException;
import com.shorturl.repository.ShortUrlRepository;
import com.shorturl.util.Base62;

@Service
public class ShortUrlService {
	
	@Autowired
	private ShortUrlRepository repository;
	
	private String label;
	
	public void saveUrl(String customLabel, String originalUrl) {
		this.label = customLabel;
		if(label == null) {
			long lastIndex = repository.count();
			this.label = generateHash(lastIndex);
			repository.save(new ShortUrl(this.label, originalUrl));
		} else {
			if(repository.findByShortUrlLabel(label) != null) {
				throw new LabelAlreadyExistsException(label);
			} else {
				repository.save(new ShortUrl(label, originalUrl));
			}
		}
	}
	
	private String generateHash(long lastIndex) {
		String generatedHash = convertToBase62(lastIndex);
		while(repository.findByShortUrlLabel(generatedHash) != null) {
			lastIndex++;
			generatedHash = convertToBase62(lastIndex);
		}
		return generatedHash;
	}
	
	private String convertToBase62(long lastIndex) {
		return Base62.convertDecimalToBase62(lastIndex);
	}

	public void updateViews(ShortUrl url) {
		final long views = url.getViews() + 1;
		url.setViews(views);
		repository.save(url);
	}
	
	public ShortUrl getShortUrlByLabel(String label) {
		if (label == null) {
			throw new UninformedLabelException();
		}
		ShortUrl shortUrl = repository.findByShortUrlLabel(label);
		if(shortUrl == null) {
			throw new ShortenedUrlNotFoundException(label);
		}
		return shortUrl;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
}
