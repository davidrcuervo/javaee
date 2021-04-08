package com.laetienda.frontend.tomcat;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.frontend.engine.Settings;
import com.laetienda.model.webdb.ThankyouPage;

public class Load implements ServletContextListener {
	final private static Logger log = LogManager.getLogger(ServletContextListener.class);

	private Settings settings;
	
    public Load() {
        
    }

    public void contextDestroyed(ServletContextEvent arg0)  { 

    }

    public void contextInitialized(ServletContextEvent arg0)  { 
         
    	settings = new Settings();
    	System.out.println("$frontend.template: " + settings.get("frontend.template"));
    	ServletContext sc = arg0.getServletContext();
    	sc.setAttribute("settings", settings);
    	log.debug("Settings object has sticked to servlet context");
    	
    	Map<String, ThankyouPage> dbthk = new HashMap<String, ThankyouPage>();
    	sc.setAttribute("dbthk", dbthk);
    	log.debug("Thakyou database map has been sticked to servlet context");
    	
    	log.info("Servlet Context, Load, has initialized succesfully");
    }
}
