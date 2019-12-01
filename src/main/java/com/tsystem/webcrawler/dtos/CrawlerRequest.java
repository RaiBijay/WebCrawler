package com.tsystem.webcrawler.dtos;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tsystem.webcrawler.enums.HttpReqType;

import lombok.Data;

@Data
@JsonIgnoreProperties(value = { "params", "headers" })
public class CrawlerRequest {
	String baseUrl;
	Integer depthValue;
	HttpReqType httpReqType;
	Boolean isBaseUrl;
	Map<String, String> params;
	Map<String, String> headers;
}
