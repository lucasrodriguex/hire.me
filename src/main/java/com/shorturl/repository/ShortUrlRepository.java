package com.shorturl.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shorturl.entity.ShortUrl;

@Repository
public interface ShortUrlRepository extends CrudRepository<ShortUrl,Long>{

	ShortUrl findByShortUrlLabel(String label);
	List<ShortUrl> findTop10ByOrderByViewsDesc();
	
	
	//https://docs.spring.io/spring/docs/current/spring-framework-reference/html/orm.html#orm-hibernate-tx-declarative
	@Transactional
	void deleteByShortUrlLabel(String label);
}
