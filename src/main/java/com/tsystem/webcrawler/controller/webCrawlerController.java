package com.tsystem.webcrawler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tsystem.webcrawler.dtos.ClientRequest;
import com.tsystem.webcrawler.dtos.ClientResponse;
import com.tsystem.webcrawler.dtos.CrawlerRequest;
import com.tsystem.webcrawler.dtos.CrawlerResponse;
import com.tsystem.webcrawler.service.CrawlService;

@RestController
public class webCrawlerController {

	@Autowired
	CrawlService crawlService;

	@PostMapping(value = "/crawl")
	public ResponseEntity<ClientResponse<CrawlerResponse>> crawl(@RequestBody ClientRequest<CrawlerRequest> request) {
		return ResponseEntity.ok().body(crawlService.crawl(request.getData()));
	}
}
