package com.laetienda.lib.tomcat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebEngine {
	private final static Logger log = LogManager.getLogger(WebEngine.class);
	
	private List<String> styles;
	private List<String> scripts;
	private HttpServletRequest req;
	private String title;
	private String active = null;
	
	public WebEngine() {
		
	}
	
	public WebEngine(HttpServletRequest req) {
		this.req = req;
	}

	public void setHttpServletRequest(HttpServletRequest req) {
		this.req = req;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	private String hrefbuilder(String uri, String app) {
		
		String scheme = req.getHeader("x-forwarded-proto") == null ? req.getScheme() : req.getHeader("x-forwarded-proto");
		int port = req.getHeader("X-Forwarded-Port") == null ? req.getServerPort() : Integer.parseInt(req.getHeader("X-Forwarded-Port")); 
		String result = scheme + "://" + req.getServerName();
				
		if(
				scheme.toLowerCase().equals("http") && port == 80 ||
				scheme.toLowerCase().equals("https") && port == 443
				) {
			
		}else {
			result += ":" + port;
		}
		
		if(app == null) {
			result += req.getContextPath();
		}else {
			result += app;
		}
		
		if(uri == null || uri.isBlank()) {
			log.warn("Request url is null or empty");
			result += req.getServletPath();
		}else if(uri.charAt(0) == ('/')) {
			result = result + uri;
		}else{
			result = req.getServletPath() + "/" + uri;
		}
		
		return result; // + ";jsessionid=" + req.getSession(true).getId();
	}
	
	public String href(String uri) {
		return hrefbuilder(uri, null);
	}
	
	public String apphref(String uri, String app) {
		return hrefbuilder(uri, "/" + app);
	}
	
	public void setUriStyle(String style) {
		setStyle(href(style));
	}
	
	public void setStyle(String style) {
		if(styles == null)
			styles = new ArrayList<String>();
		
		if(!styles.contains(style) && style != null && !style.isBlank())
			styles.add(style);
	}
	
	public void setUriScript(String script) {
		setScript(href(script));
	}
	
	public void setScript(String script) {
		if(scripts == null) {
			scripts = new ArrayList<String>();
		}
		
		if(scripts.contains(script) || script == null || script.isBlank()) {
			log.debug("Sript already has been added. $script: {}", script);
		}else {
			scripts.add(script);
		}
	}
	
	public List<String> getStyles(){
		return styles;
	}
	
	public List<String> getScripts(){
		return scripts;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
	
	
}
