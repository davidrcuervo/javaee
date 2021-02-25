package com.laetienda.wiki.tomcat;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.laetienda.wiki.lib.Settings;

public class Load implements ServletContextListener {

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
    }
}
