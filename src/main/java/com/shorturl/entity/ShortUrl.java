package com.shorturl.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ShortUrl implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;
	
	private String shortUrlLabel;
	private String originalUrl;
	private Long views;
	
	public ShortUrl() {}
	
	public ShortUrl (String label, String originalUrl) {
		this.shortUrlLabel = label;
		this.originalUrl = originalUrl;
		this.views = 0L;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShortUrlLabel() {
		return shortUrlLabel;
	}

	public void setShortUrlLabel(String shortUrl) {
		this.shortUrlLabel = shortUrl;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public Long getViews() {
		return views;
	}

	public void setViews(Long views) {
		this.views = views;
	}
}
