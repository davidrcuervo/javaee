package com.laetienda.lib.bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.utilities.HttpQuickClient;

public class HttpTemplate {
	private final static Logger log = LogManager.getLogger(HttpTemplate.class);
	
	private String url;
	private String uriHeader;
	private String uriMenu;
	private String username;
	private String password;
	private HttpQuickClient httpClient;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
		setAuthentication();
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		setAuthentication();
	}
	
	private void setAuthentication() {
		if(password != null && username != null && !password.isBlank() && !username.isBlank()) {
			httpClient.setAuthHeader(username, password);
		}
	}

	public HttpTemplate() {
		httpClient = new HttpQuickClient();
	}
	
	public String getUriHeader() {
		return uriHeader;
	}

	public void setUriHeader(String uriHeader) {
		this.uriHeader = uriHeader;
	}

	public String getUriMenu() {
		return uriMenu;
	}

	public void setUriMenu(String uriMenu) {
		this.uriMenu = uriMenu;
	}
	
	public void setTitle(String title) {
		httpClient.setPostParameter("title", title);
	}
	
	public void setStyle(String style) {
		httpClient.setPostParameter("style", style);
	}
	
	public void setActive(String action) {
		httpClient.setPostParameter("active", action);
	}
	
	public void setScript(String script) {
		httpClient.setPostParameter("script", script);
	}
		
	public String getHeader() {
		log.debug("getting header from template frontend");
		return httpClient.post(this.url + this.uriHeader);
	}
	
	public String getMenu() {
		log.debug("getting menu from template frontend");
		return httpClient.post(this.url + this.uriMenu);
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
			
	public static void main(String[] args) {
		HttpTemplate template = new HttpTemplate();
		template.setUrl("http://localhost:8080/frontend0/template/main");
		template.setUriHeader("/header");
		template.setUriMenu("/menu");
		template.setActive("wiki");
		template.setTitle("titulo");
		System.out.println(template.getHeader());
		System.out.println(template.getMenu());
		
	}
}
