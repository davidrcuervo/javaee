package com.laetienda.frontend.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.tomcat.WebEngine;

public class TemplateController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(TemplateController.class);
       
	String[] pathParts;
	WebEngine web;
	
    public TemplateController() {
        super();
        
    }
    
    private void doBuild(HttpServletRequest request) {
    	pathParts = (String[])request.getAttribute("pathParts");
    	web = (WebEngine)request.getAttribute("web");
    	
    	log.debug("pathParts.length: " + pathParts.length);
		log.debug("pathParts[0].length: " + pathParts[0].length());
		
		for(int c=0; c < pathParts.length; c++){
			log.debug("pathParts[" + c + "]: " + pathParts[c]);
		}
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid url request");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);
		
		if(pathParts.length == 2 && !pathParts[0].isBlank() && !pathParts[1].isBlank()) {
			String template = pathParts[0];
			String part = pathParts[1];
			
			web.setTitle(request.getParameter("title"));
			web.setActive(request.getParameter("active"));
			
			if(request.getParameter("script") != null && !request.getParameter("script").isBlank()) {
				for(String script : request.getParameterValues("script")) {
					web.setScript(script);
				}
			}
			
			if(request.getParameter("script") != null && !request.getParameter("script").isBlank()) {
				for(String style : request.getParameterValues("style")) {
					web.setStyle(style);
				}
			}
			
			log.debug("Post title: {}", request.getParameter("title"));
			
			Map<String,String[]> parameters = request.getParameterMap();
			
			for(Map.Entry<String,String[]> entry: parameters.entrySet()){
				log.debug("key: {}", entry.getKey());
				for(String value : entry.getValue()) {
					log.debug("value: {}", value);
				}
			}
			
			request.getRequestDispatcher("/WEB-INF/jsp/template/" + template +"/" + part + ".jsp").forward(request, response);
			
		}else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid url request");
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid url request");
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid url request");
	}

}
