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

public class ApplicationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger log = LogManager.getLogger(ApplicationController.class);
       
	private Settings settings;
	private String[] pathParts;
	
    public ApplicationController() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
		settings = (Settings)config.getServletContext().getAttribute("settings");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		pathParts = (String[])request.getAttribute("pathParts");
		
		if(pathParts[0].length() > 0) {
			
			String app = "app." + pathParts[0] + ".url";
			String appUrl = settings.get(app);
			log.debug("The app is: {} -> and url is: {}", app, appUrl);
			
			if(appUrl != null) {
				
				for(int c = 1; c < pathParts.length; c++) {
					appUrl += "/" + pathParts[c];
				}
				
				response.sendRedirect(appUrl);
			}else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			
		}else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
