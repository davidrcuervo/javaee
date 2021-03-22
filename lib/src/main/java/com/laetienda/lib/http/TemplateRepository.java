  package com.laetienda.lib.http;

public interface TemplateRepository {

	void setPostParameter(String key, String Value);
	void setCookieDomain(String cookieDomain);
	void setUrl(String url);
	String getHtmlTemplate(String url);
}