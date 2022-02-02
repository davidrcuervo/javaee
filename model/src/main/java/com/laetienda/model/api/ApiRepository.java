package com.laetienda.model.api;

import java.net.URL;
import java.util.Map;

import com.laetienda.lib.http.HttpClientException;

public interface ApiRepository {
	
	public void setVisitor(String visitor);
	public String getVisitor();
	public Object call(Map<String, String> params, Class<?> type, HttpMethod httpMethod, URL url) throws HttpClientException;
	public Object call(Map<String, String> params, Class<?>... paramTypes) throws HttpClientException;
}
