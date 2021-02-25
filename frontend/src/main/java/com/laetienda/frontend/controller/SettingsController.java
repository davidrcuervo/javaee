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

public class SettingsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger log = LogManager.getFormatterLogger(SettingsController.class);
	
	private Settings settings;
	private String[] pathParts;
       
    public SettingsController() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
		settings = (Settings)config.getServletContext().getAttribute("settings");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		pathParts = (String[])request.getAttribute("pathParts");
		log.debug("$pathParts[0]: " + pathParts[0]);
		log.debug("pathParts[0].isBlank(): " + (pathParts[0].isBlank() ? "true" : "false"));
		
		if(pathParts.length == 1 && !pathParts[0].isBlank()) {
			request.getServletContext().getRequestDispatcher("/WEB-INF/jsp/settings/" + pathParts[0] + ".jsp").forward(request, response);
		}else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
