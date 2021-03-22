package com.laetienda.webdb.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Html extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static private Logger log = LogManager.getLogger(Html.class);
    final private String JSP_PATH = "/WEB-INF/html";
	
    public Html() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "").replaceFirst(".html", ".jsp");
		log.debug("$path: {}", path);
//		log.debug("$request.getURI: {}", request.getRequestURI());
//		log.debug("$request.getURL: {}", request.getRequestURL());
//		log.debug("$request.getContextPath(): {}", request.getContextPath());
//		log.debug("$request.getRequestURI().replaceFirst(request.getContextPath(): {}", request.getRequestURI().replaceFirst(request.getContextPath(), "").replaceFirst(".html", ".jsp"));
		
		request.getRequestDispatcher(JSP_PATH + path).forward(request, response);
	}
}
