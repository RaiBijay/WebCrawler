package com.tsystem.webcrawler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tsystem.webcrawler.model.ProcessedUrl;

@Repository
public interface ProcessedUrlRepository extends JpaRepository<ProcessedUrl, Long> {
	@Query("FROM process_url WHERE url = :url AND processedStatus =:processedStatus")
	public ProcessedUrl findByProcessedUrlAndProcessedStatus(@Param("url") String url, @Param("processedStatus") Boolean processedStatus);

}