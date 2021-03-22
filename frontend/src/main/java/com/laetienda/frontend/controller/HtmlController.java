package com.laetienda.frontend.controller;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.frontend.engine.Settings;
import com.laetienda.lib.tomcat.WebEngine;

public class HtmlController extends HttpServlet {
	final private static String HTML_PATH = "/WEB-INF/html";
	final private static Logger log = LogManager.getLogger(HtmlController.class);
	private static final long serialVersionUID = 1L;
	
	private Settings settings;
	
    public HtmlController() {
        super();
    }

    public void init(ServletConfig sc) {
    	settings = (Settings)sc.getServletContext().getAttribute("settings");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		WebEngine web = (WebEngine)request.getAttribute("web");
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "").replaceFirst(".html", ".jsp");
		String template = settings.get("frontend.template");
		log.debug("$htmlPath: {}", path);
		log.debug("$frontend.template: {}", template);
		
		if(path.equals("/")) {
			response.sendRedirect(web.href("/home.html"));
		}else {			
			request.getRequestDispatcher(HTML_PATH + "/" + template + path).forward(request, response);
		}
		
	}

}
