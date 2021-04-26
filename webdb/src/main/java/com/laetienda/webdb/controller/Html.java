package com.laetienda.webdb.controller;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.tomcat.WebEngine;
import com.laetienda.webdb.lib.Settings;

@Deprecated
public class Html extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static private Logger log = LogManager.getLogger(Html.class);
    final private String JSP_PATH = "/WEB-INF/html";
	
    private String template;
    
    public Html() {
        super();
    }
    
    @Override
    public void init(ServletConfig sc) {
    	Settings settings = (Settings)sc.getServletContext().getAttribute("settings");
    	template = settings.get("template");
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "").replaceFirst(".html", ".jsp");
		WebEngine web = (WebEngine)request.getAttribute("web");

		log.debug("$path: {}", path);
		log.debug("$template: {}", template);
		
		
		if(path.equals("/")) {
			response.sendRedirect(web.href("/home.html"));
		}else {
			request.getRequestDispatcher(JSP_PATH + "/" + template + path).forward(request, response);			
		}
		
//		log.debug("$request.getURI: {}", request.getRequestURI());
//		log.debug("$request.getURL: {}", request.getRequestURL());
//		log.debug("$request.getContextPath(): {}", request.getContextPath());
//		log.debug("$request.getRequestURI().replaceFirst(request.getContextPath(): {}", request.getRequestURI().replaceFirst(request.getContextPath(), "").replaceFirst(".html", ".jsp"));
		
	}
}
