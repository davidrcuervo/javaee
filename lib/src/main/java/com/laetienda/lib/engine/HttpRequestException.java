package com.laetienda.lib.engine;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

public class HttpRequestException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private CloseableHttpResponse response;
	
	HttpRequestException(CloseableHttpResponse response, String message){
		super(message);
		this.response = response;
	}

	public CloseableHttpResponse getResponse() {
		return response;
	}
}
