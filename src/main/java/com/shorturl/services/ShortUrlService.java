package com.shorturl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shorturl.entity.ShortUrl;
import com.shorturl.repository.ShortUrlRepository;

@Service
public class ShortUrlService {
	
	@Autowired
	private ShortUrlRepository shortUrlRepository;
	
	public void save(String customLabel, String originalUrl) {
		if(customLabel == null) {
			long lastIndex = shortUrlRepository.count();
			String generatedHash = generateHash(lastIndex);
			shortUrlRepository.save(new ShortUrl(generatedHash, originalUrl, 0L));
		} else {
			if(shortUrlRepository.findByShortUrlLabel(customLabel) != null) {
				shortUrlRepository.save(new ShortUrl(customLabel, originalUrl, 0L));
			} else {
				throw new RuntimeException();
			}
		}
	}
	
	private String generateHash(long lastIndex) {
		String generatedHash = convertToBase62(lastIndex);
		while(shortUrlRepository.findByShortUrlLabel(generatedHash) != null) { //verifica se ja existe uma label igual
			lastIndex++;
			generatedHash = convertToBase62(lastIndex);
		}
		return generatedHash;
	}
	
	private String convertToBase62(long lastIndex) {
		return "";
	}

	public void updateViews(ShortUrl url) {
		final long views = url.getViews() + 1;
		url.setViews(views);
		shortUrlRepository.save(url);
	}
}
