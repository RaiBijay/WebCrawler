package com.tsystem.webcrawler.utilities;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystem.webcrawler.dtos.CrawlerRequest;
import com.tsystem.webcrawler.dtos.CrawlerResponse;
import com.tsystem.webcrawler.dtos.SubLinkCrawlDetail;

public class HttpRequestHandler {

	public static CrawlerResponse handlePostRequest(CrawlerRequest crawlerRequest) throws IOException {
		CrawlerResponse crawlResponse=new CrawlerResponse();
		SubLinkCrawlDetail subLinkCrawlDetail = null;
		HttpPost postRequest = null;
		StringBuilder requestBody = new StringBuilder();
		if (crawlerRequest != null) {
			if (crawlerRequest.getBaseUrl() != null && !crawlerRequest.getBaseUrl().isEmpty()) {
				postRequest = new HttpPost(crawlerRequest.getBaseUrl());
			}
			if(crawlerRequest.getHeaders()!=null && !crawlerRequest.getHeaders().isEmpty()) {
				for (Map.Entry<String, String> entry : crawlerRequest.getHeaders().entrySet()) {
					postRequest.addHeader(entry.getKey(),entry.getValue());
				}
			}
			if (crawlerRequest.getParams() != null && !crawlerRequest.getParams().isEmpty()) {
				int counter = 0;
				for (Map.Entry<String, String> entry : crawlerRequest.getParams().entrySet()) {
					counter++;
					if (counter < crawlerRequest.getParams().size()) {
						requestBody.append("\"" + entry.getKey() + "\":" + "\"" + entry.getValue() + "\",");
					} else {
						requestBody.append("\"" + entry.getKey() + "\":" + "\"" + entry.getValue() + "\"");
					}
				}
				postRequest.setEntity(new StringEntity(requestBody.toString()));
			}
		}
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse httpResponse = httpClient.execute(postRequest);) {
			if(crawlerRequest.getIsBaseUrl()) {
				crawlResponse=processBaseUrl(httpResponse);
			}else {
				subLinkCrawlDetail = processSubUrlResponse(httpResponse);
				if(subLinkCrawlDetail!=null) {
					crawlResponse.getSubLinkDetails().add(subLinkCrawlDetail);
				}
			}
			
		} catch (Exception exception) {

		}
		return crawlResponse;
	}

	public static CrawlerResponse handleGetRequest(CrawlerRequest crawlerRequest) {
		HttpGet getRequest =null;
		CrawlerResponse crawlResponse=new CrawlerResponse();
		SubLinkCrawlDetail subLinkCrawlDetail = null;
		if(crawlerRequest!=null) {
			if (crawlerRequest.getBaseUrl() != null && !crawlerRequest.getBaseUrl().isEmpty()) {
				getRequest = new HttpGet(crawlerRequest.getBaseUrl());
			}
			if(crawlerRequest.getHeaders()!=null && !crawlerRequest.getHeaders().isEmpty()) {
				for (Map.Entry<String, String> entry : crawlerRequest.getHeaders().entrySet()) {
					getRequest.addHeader(entry.getKey(),entry.getValue());
				}
			}
		}
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
		    CloseableHttpResponse httpResponse = httpClient.execute(getRequest);) {
			if(crawlerRequest.getIsBaseUrl()) {
				crawlResponse=processBaseUrl(httpResponse);
			}else {
				subLinkCrawlDetail = processSubUrlResponse(httpResponse);
				if(subLinkCrawlDetail!=null) {
					crawlResponse.getSubLinkDetails().add(subLinkCrawlDetail);
				}
			}
		} catch (Exception exception) {

		}
		return crawlResponse;
	}

	private static SubLinkCrawlDetail processSubUrlResponse(CloseableHttpResponse closableHttpResponse) {
		System.out.println(closableHttpResponse.toString());
		String response = null;
		SubLinkCrawlDetail subCrawlDetail=null;
		try {
			response = EntityUtils.toString(closableHttpResponse.getEntity());
			ObjectMapper objectMapper=new ObjectMapper();
			subCrawlDetail=objectMapper.readValue(response, SubLinkCrawlDetail.class);
		} catch (IOException ioException) {

		}
		return subCrawlDetail;
	}
	
	private static CrawlerResponse processBaseUrl(CloseableHttpResponse closableHttpResponse) {
		System.out.println(closableHttpResponse.toString());
		String response = null;
		CrawlerResponse baseCrawlDetail=null;
		try {
			response = EntityUtils.toString(closableHttpResponse.getEntity());
			ObjectMapper objectMapper=new ObjectMapper();
			baseCrawlDetail=objectMapper.readValue(response, CrawlerResponse.class);
		} catch (IOException ioException) {

		}
		return baseCrawlDetail;
	}

}
