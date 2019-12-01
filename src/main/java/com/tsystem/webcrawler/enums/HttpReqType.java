package com.tsystem.webcrawler.enums;

public enum HttpReqType {
	POST("post"), GET("get");

	private String name;

	public String getName() {
		return this.name;
	}

	private HttpReqType(String name) {
		this.name = name;
	}
}
