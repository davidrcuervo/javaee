package com.laetienda.lib.http;

public interface ClientRepository {

	public void setAuthHeader(String username, String password);
	public void setPostParameter(String key, String value);
	public String getPostParameter(String key);
	public void removePostParameter(String key);
	public void setCookie(String key, String value, String domain);
	public String post(String url);
	public String get(String url);

}