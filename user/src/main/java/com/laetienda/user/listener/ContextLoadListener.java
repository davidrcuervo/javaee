package com.laetienda.user.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.user.lib.Settings;



public class ContextLoadListener implements ServletContextListener {
	
	private final static Logger log = LogManager.getLogger(ContextLoadListener.class);
	private Settings settings;
	
    public ContextLoadListener() {
        
    }

    public void contextDestroyed(ServletContextEvent sce)  { 
         
    }

    public void contextInitialized(ServletContextEvent sce)  { 

    	ServletContext sc = sce.getServletContext();
    	
    	settings = new Settings();
    	sc.setAttribute("settings", settings);
    	log.debug("Contest Load Listener has started succesfully");
    }
	
}
