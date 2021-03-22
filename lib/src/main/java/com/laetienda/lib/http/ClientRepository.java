package com.laetienda.lib.http;

public interface ClientRepository {

	void setAuthHeader(String username, String password);

	void setPostParameter(String key, String value);

	void setCookie(String key, String value, String domain);

	String post(String url);

	String get(String url);

}