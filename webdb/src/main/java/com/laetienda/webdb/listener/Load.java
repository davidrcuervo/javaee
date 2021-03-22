package com.laetienda.webdb.listener;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.webdb.lib.Settings;

public class Load implements ServletContextListener {
	final static private Logger log = LogManager.getLogger(Load.class);
	

    public Load() {
        
    }

    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    }

    public void contextInitialized(ServletContextEvent sce)  { 

    	ServletContext sc = sce.getServletContext();
    	Settings settings = new Settings();
    	
    	sc.setAttribute("settings", settings);
    	log.debug("Settings attribute has been attached to WebDb Servlet Context");
    	
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.laetienda.webdb");
    	sc.setAttribute("emf", emf);
    	log.debug("Entity Manager Factory has been attached to WebDb Servlet Context");
    	
    	log.info("Tomcat Servlet context for WebDb has initialized correctly");
    }
	
}
