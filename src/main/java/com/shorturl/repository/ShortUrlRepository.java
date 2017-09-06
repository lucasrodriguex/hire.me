package com.shorturl.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shorturl.entity.ShortUrl;

@Repository
public interface ShortUrlRepository extends CrudRepository<ShortUrl,Long>{

	ShortUrl findByShortUrlLabel(String label);
}
