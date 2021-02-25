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

	
	public String href(String uri) {
		String result = req.getScheme() + "://" + req.getServerName();
		
		if(
				req.getScheme().toLowerCase().equals("http") && req.getServerPort() == 80 ||
				req.getScheme().toLowerCase().equals("https") && req.getServerPort() == 443
				) {
			
		}else {
			result += ":" + req.getServerPort();
		}
		
		result += req.getContextPath();
		
		if(uri == null || uri.isBlank()) {
			log.warn("Request url is null or empty");
			result += req.getServletPath();
		}else if(uri.charAt(0) == ('/')) {
			result = result + uri;
		}else{
			result = req.getServletPath() + "/" + uri;
		}
		
		return result;
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
