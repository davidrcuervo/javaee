package com.laetienda.wiki.tomcat;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.wiki.lib.Settings;

public class Load implements ServletContextListener {
	private static final Logger log = LogManager.getLogger();
	private Settings settings;
	
    public Load() {
        settings = new Settings();
    }

    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    }

    public void contextInitialized(ServletContextEvent sce)  {
    	
    	ServletContext sc = sce.getServletContext();
    	
    	sc.setAttribute("settings", settings);
    	log.debug("settings -> $frontend.template.main.url: {}" , settings.get("frontend.template.main.url"));
    	log.debug("Wike application Load has started succesfully");
    
	}
}
