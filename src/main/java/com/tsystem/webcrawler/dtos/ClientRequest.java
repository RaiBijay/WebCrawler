package com.tsystem.webcrawler.dtos;

import lombok.Data;

@Data
public class ClientRequest<T> {
	T data;
}
