package com.laetienda.backend.tomcat;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class FilterPath implements Filter{
	
	final static Logger log = LogManager.getLogger(FilterPath.class);
	
	public void init(FilterConfig fConfig) throws ServletException{
	
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpReq = (HttpServletRequest)request;
		String uri = httpReq.getRequestURI();
		
		String urlPattern = httpReq.getContextPath() + httpReq.getServletPath();
		
		String path = new String();
		
		int index = uri.indexOf('&');
		
		if(index < 0){
			index = uri.length();
		}
		
		try{
			path = uri.substring(urlPattern.length() + 1, index);
		}catch (IndexOutOfBoundsException ex){
			
		}
		
		String[] pathParts = path.split("/");
		String[] allpathParts = uri.substring(1, index).split("/");
		
		httpReq.setAttribute("pathParts", pathParts);
		httpReq.setAttribute("allpathParts", allpathParts);
		
		
		/*
		 * DISABLE BLOCK BELOW, ONLY ENABLE IT TO TROUBLESHOOT PATH PARTS MODULE
		 */
		
		log.debug("uri: " + uri);
		log.debug("urlConext: {}", httpReq.getContextPath());
		log.debug("urlPattern: " + urlPattern);
		log.debug("path: " + path);
		
		log.debug("pathParts.length: " + pathParts.length);
		log.debug("pathParts[0].length: " + pathParts[0].length());
		
		for(int c=0; c < pathParts.length; c++){
			log.debug("pathParts[" + c + "]: " + pathParts[c]);
		}
		
		log.debug("allpathParts.length: " + allpathParts.length);
		log.debug("allpathParts[0].length: " + allpathParts[0].length());
		
		for(int c=0; c < allpathParts.length; c++){
			log.debug("allpathParts[" + c + "]: " + allpathParts[c]);
		}
		
		
		chain.doFilter(request, response);
	}
	
	public void destroy(){
		
	}
}
