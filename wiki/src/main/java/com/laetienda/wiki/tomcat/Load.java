package com.laetienda.wiki.tomcat;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.wiki.lib.Settings;
import com.laetienda.wiki.repository.WikiOnFileIndexRepositoryImpl;
import com.laetienda.wiki.service.WikiIndexService;
import com.laetienda.wiki.service.WikiOnFileIndexServiceImpl;

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
    	
    	//Find wikis on init params
    	
    	WikiIndexService service = new WikiOnFileIndexServiceImpl();
    	WikiOnFileIndexRepositoryImpl wikiIndex = (WikiOnFileIndexRepositoryImpl)service.get();
    	Enumeration<String> params = sc.getInitParameterNames();
    	
    	while(params.hasMoreElements()) {
    		String param = params.nextElement();
    		String path = sc.getInitParameter(param);
    		wikiIndex.add(param, path);
    	}
    	
    	sc.setAttribute("wikiIndex", wikiIndex);
    	log.debug("Wiki Index has started succesfully");
    
    	log.debug("Wike application Load has started succesfully");
	}
}
