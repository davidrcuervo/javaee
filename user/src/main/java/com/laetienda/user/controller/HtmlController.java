package com.laetienda.user.controller;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.tomcat.WebEngine;
import com.laetienda.user.lib.Settings;

public class HtmlController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(HtmlController.class);
	
	private Settings settings;
	private String template;
	
    public HtmlController() {
        super();
        
    }

	public void init(ServletConfig config) throws ServletException {
		settings = (Settings)config.getServletContext().getAttribute("settings");
		template = settings.get("template");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("$template: {}", template);
		
		WebEngine web = (WebEngine)request.getAttribute("web");
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "").replaceFirst(".html", ".jsp");
		
		log.debug("$path: {}", path);
		
		if(path.equals("/")) {
			response.sendRedirect(web.href("/home.html"));
		}else {
			request.getRequestDispatcher("/WEB-INF/html/" + template + path).forward(request, response);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}
}
