package com.laetienda.frontend.tomcat;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.frontend.engine.Settings;

public class Load implements ServletContextListener {
	final private static Logger log = LogManager.getLogger(ServletContextListener.class);

	private Settings settings;
	
    public Load() {
        settings = new Settings();
    }

    public void contextDestroyed(ServletContextEvent arg0)  { 

    }

    public void contextInitialized(ServletContextEvent arg0)  { 
         
    	ServletContext sc = arg0.getServletContext();
    	sc.setAttribute("settings", settings);
    	log.info("Servlet Context, Load, has initialized succesfully");
    }
}
