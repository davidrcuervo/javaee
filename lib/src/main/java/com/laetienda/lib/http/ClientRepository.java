package com.laetienda.lib.http;

public interface ClientRepository {

	public void setAuthHeader(String username, String password);
	public void setPostParameter(String key, String value);
	public String getPostParameter(String key);
	public void setGetParameter(String key, String value);
	public void removePostParameter(String key);
	public void setCookie(String key, String value, String domain);
	public String post(String url) throws HttpClientException;
	public String get(String url) throws HttpClientException;
	public String put(String url) throws HttpClientException;
	public String delete(String url) throws HttpClientException;

}