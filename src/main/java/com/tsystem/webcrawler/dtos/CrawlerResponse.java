package com.tsystem.webcrawler.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrawlerResponse {
	private Integer totalLinks;
	private Integer totalImages;
    private List<String> urls=new ArrayList<>();
	private List<SubLinkCrawlDetail> subLinkDetails=new ArrayList<>();
}
