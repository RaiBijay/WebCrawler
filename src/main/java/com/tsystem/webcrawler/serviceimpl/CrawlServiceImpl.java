package com.tsystem.webcrawler.serviceimpl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tsystem.webcrawler.dtos.ClientResponse;
import com.tsystem.webcrawler.dtos.CrawlerRequest;
import com.tsystem.webcrawler.dtos.CrawlerResponse;
import com.tsystem.webcrawler.dtos.SubLinkCrawlDetail;
import com.tsystem.webcrawler.model.ProcessedUrl;
import com.tsystem.webcrawler.proccess.CrawlerWorkerThread;
import com.tsystem.webcrawler.repository.ProcessedUrlRepository;
import com.tsystem.webcrawler.service.CrawlService;
import com.tsystem.webcrawler.utilities.HttpRequestProcessor;

@Service
public class CrawlServiceImpl implements CrawlService {
	
	private static final Logger logger=LoggerFactory.getLogger(CrawlServiceImpl.class);
	
	@Autowired
	ProcessedUrlRepository processedUrlRepository;

	@Value("${crawler.worker.corePoolSize}")
	Integer corePoolSize;

	@Value("${crawler.worker.maxPoolSize}")
	Integer maxPoolSize;

	@Value("${crawler.worker.keepAliveTime}")
	Integer keepAliveTime;

	Set<String> urlList = new HashSet<>();

	Set<String> imageList = new HashSet<>();

	@Override
	public ClientResponse<CrawlerResponse> crawl(CrawlerRequest crawlerRequest) {
		ClientResponse<CrawlerResponse> clientResponse = new ClientResponse<>();
		CrawlerResponse crawlerResponse = null;
		try {
			crawlerResponse = processFirstPage(crawlerRequest);
			if (crawlerResponse != null) {
				clientResponse.setData(crawlerResponse);
			}

		} catch (Exception exception) {

		}
		return clientResponse;
	}

	private CrawlerResponse processFirstPage(CrawlerRequest crawlerRequest) {
		CrawlerResponse crawlerResponse = new CrawlerResponse();
		Document document = null;
		try {
			if (crawlerRequest.getBaseUrl() != null) {
				logger.info("Crawling started with base url "+crawlerRequest.getBaseUrl());
				document = HttpRequestProcessor.processRequest(crawlerRequest.getBaseUrl());
				Map<String, Elements> processLinksAndImages = HttpRequestProcessor.fetchLinkAndImg(document);
				if (processLinksAndImages != null && !processLinksAndImages.isEmpty()) {
//					processedUrl.setProcessedStatus(true);
//					processedUrl.setProcessedUrl(crawlerRequest.getBaseUrl());
//					processedUrlRepository.save(processedUrl);
					if (processLinksAndImages.get("LINKS") != null && !processLinksAndImages.get("LINKS").isEmpty()) {
						HttpRequestProcessor.fetchAttributeValueOfElement(processLinksAndImages.get("LINKS"), "href",
								urlList);
					}
					if (processLinksAndImages.get("IMAGES") != null && !processLinksAndImages.get("IMAGES").isEmpty()) {
						HttpRequestProcessor.fetchAttributeValueOfElement(processLinksAndImages.get("IMAGES"), "src",
								imageList);
					}
				}
				if (crawlerRequest.getIsBaseUrl()) {
					crawlerResponse.setTotalLinks(urlList.size());
					crawlerResponse.setTotalImages(imageList.size());
				}
			}
			processRemainingPages(crawlerResponse, crawlerRequest);
		} catch (HttpStatusException httpStatusException) {
           logger.error("Http Status Exception stack trace ",httpStatusException);
		} catch (IOException ioException) {
		   logger.error("IO Exception stack trace ",ioException);
		}
		return crawlerResponse;
	}

	private void processRemainingPages(CrawlerResponse crawlerResponse, CrawlerRequest crawlerRequest) {
		try {
			System.out.println("Inside process Remaining Pages");
			int counter = 0;
			Set<String> newUrlList = new HashSet<>();
			if(urlList.isEmpty() && counter==crawlerRequest.getDepthValue()) {
				return;
			}
			while (!urlList.isEmpty() && counter < crawlerRequest.getDepthValue()) {
				ExecutorService threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
						TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
				Iterator<String> urlIterator = urlList.iterator();
				ProcessedUrl processedUrl = processedUrlRepository.findByProcessedUrlAndProcessedStatus(crawlerRequest.getBaseUrl(), new Boolean(true));
				while (urlIterator.hasNext()) {
					if(processedUrl==null) {
						processedUrl = new ProcessedUrl();
						processedUrl.setProcessedStatus(true);
						processedUrl.setUrl(crawlerRequest.getBaseUrl());
						processedUrlRepository.save(processedUrl);
						CrawlerWorkerThread crawlerWorkerThread = new CrawlerWorkerThread();
						CrawlerRequest crawlerRqst = new CrawlerRequest();
						crawlerRqst.setBaseUrl(urlIterator.next());
						crawlerWorkerThread.setCrawlerRequest(crawlerRqst);
						crawlerWorkerThread.setUrlList(newUrlList);
						Future<SubLinkCrawlDetail> crawlResponse = threadPoolExecutor.submit(crawlerWorkerThread);
						crawlerResponse.getSubLinkDetails().add(crawlResponse.get());
						urlIterator.remove();
					}
					
				}
	           urlList=newUrlList;
	           threadPoolExecutor.shutdown();
	           System.out.println("URL List size "+urlList.size());
	           System.out.println(urlList);
	           processRemainingPages(crawlerResponse,crawlerRequest);
			   counter++;
			}
		} catch (Exception exception) {
			logger.error("Exception stack trace ",exception);
		}
	}

}
