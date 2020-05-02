package com.laetienda.frontend.engine;

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
	
	public WebEngine(HttpServletRequest req) {
		this.req = req;
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
	
	public void addStyle(String style) {
		if(styles == null)
			styles = new ArrayList<String>();
		
		if(!styles.contains(style))
			styles.add(style);
	}
	
	public void addScript(String script) {
		if(scripts == null) {
			scripts = new ArrayList<String>();
		}
		
		if(scripts.contains(script)) {
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
}
