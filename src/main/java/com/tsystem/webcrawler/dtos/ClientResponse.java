package com.tsystem.webcrawler.dtos;

import lombok.Data;

@Data
public class ClientResponse<T> {
	T data;
}
