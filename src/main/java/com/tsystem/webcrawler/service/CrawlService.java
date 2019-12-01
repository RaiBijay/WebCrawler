package com.tsystem.webcrawler.service;

import com.tsystem.webcrawler.dtos.ClientResponse;
import com.tsystem.webcrawler.dtos.CrawlerRequest;
import com.tsystem.webcrawler.dtos.CrawlerResponse;

public interface CrawlService {
      public ClientResponse<CrawlerResponse> crawl(CrawlerRequest crawlerRequest);
}
