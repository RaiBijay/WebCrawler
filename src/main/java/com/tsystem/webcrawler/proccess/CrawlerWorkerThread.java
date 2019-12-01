package com.tsystem.webcrawler.proccess;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tsystem.webcrawler.dtos.CrawlerRequest;
import com.tsystem.webcrawler.dtos.SubLinkCrawlDetail;
import com.tsystem.webcrawler.model.ProcessedUrl;
import com.tsystem.webcrawler.repository.ProcessedUrlRepository;
import com.tsystem.webcrawler.utilities.HttpRequestProcessor;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class CrawlerWorkerThread implements Callable<SubLinkCrawlDetail> {
	private static final Logger logger=LoggerFactory.getLogger(CrawlerWorkerThread.class);

	private CrawlerRequest crawlerRequest;
	private Set<String> urlList = new HashSet<>();
	private Set<String> imageList = new HashSet<>();

	@Override
	public SubLinkCrawlDetail call() {
		System.out.println("Execution started ");
		SubLinkCrawlDetail crawlerResponse = new SubLinkCrawlDetail();
		try {
			if (crawlerRequest.getBaseUrl() != null) {
				logger.info("Get base url "+crawlerRequest.getBaseUrl());
					Document document = HttpRequestProcessor.processRequest(crawlerRequest.getBaseUrl());
					Map<String, Elements> processLinksAndImages = HttpRequestProcessor.fetchLinkAndImg(document);
					if (processLinksAndImages != null && !processLinksAndImages.isEmpty()) {
						if (processLinksAndImages.get("LINKS") != null && !processLinksAndImages.get("LINKS").isEmpty()) {
							HttpRequestProcessor.fetchAttributeValueOfElement(processLinksAndImages.get("LINKS"), "href", urlList);
						}
						if (processLinksAndImages.get("IMAGES") != null && !processLinksAndImages.get("IMAGES").isEmpty()) {
							HttpRequestProcessor.fetchAttributeValueOfElement(processLinksAndImages.get("IMAGES"), "src", imageList);
						}
					}
					Elements element = document.select("title");
					if (element != null) {
						crawlerResponse.setPageTitle(element.text());
					}
					crawlerResponse.setImageCount(imageList.size());
					crawlerResponse.setPageLink(crawlerRequest.getBaseUrl());

			}
		} catch (IOException ioException) {
			logger.error("IO Exception occured while processing thread ", ioException);
		} catch (Exception exception) {
			logger.error("Exception occured while processing thread ", exception);
		}

		return crawlerResponse;
	}

}
