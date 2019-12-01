package com.tsystem.webcrawler.dtos;

import java.util.List;

import lombok.Data;

@Data
public class SubLinkCrawlDetail {
	private String pageTitle;
	private String pageLink;
	private Integer imageCount;
	private List<SubLinkCrawlDetail> subLinkCrawlDetail;
}
