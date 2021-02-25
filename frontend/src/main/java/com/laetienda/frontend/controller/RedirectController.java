package com.laetienda.frontend.controller;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.tomcat.WebEngine;

public class RedirectController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final static Logger log = LogManager.getLogger(RedirectController.class);
    
    public RedirectController() {
        super();
    }
   
    
    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private void redirect(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	WebEngine web = (WebEngine)req.getAttribute("web");
    	String path = req.getServletPath();
    	String result = "";
    	
    	res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    	log.debug("Redirecting url. $uri: {}", path);
    	  	
    	switch(path) {
	    	default:
	    		result= web.href("/home");
    	}
    	
    	res.sendRedirect(result);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		redirect(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		redirect(request, response);
	}

}
