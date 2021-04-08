  package com.laetienda.lib.http;

public interface TemplateRepository {

	public void setPostParameter(String key, String Value);
	public void removePostParameter(String key);
	public void setCookieDomain(String cookieDomain);
	public void setUrl(String url);
	public String getHtmlTemplate(String url);
	public String getPostParameter(String key);
}