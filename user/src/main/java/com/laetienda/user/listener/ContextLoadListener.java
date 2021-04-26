package com.laetienda.user.listener;

import java.security.GeneralSecurityException;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.user.lib.Settings;
import com.laetienda.user.repository.UsuarioJndiRepoImpl;
import com.laetienda.user.repository.UsuarioRepository;

public class ContextLoadListener implements ServletContextListener {
	
	private final static Logger log = LogManager.getLogger(ContextLoadListener.class);
	private Settings settings;
	private EntityManagerFactory emf;
	
    public ContextLoadListener() {
        
    }

    public void contextDestroyed(ServletContextEvent sce)  { 
         emf.close();
    }

    public void contextInitialized(ServletContextEvent sce)  { 

    	ServletContext sc = sce.getServletContext();
    	
    	settings = new Settings();
    	sc.setAttribute("settings", settings);
    	
    	emf = Persistence.createEntityManagerFactory("com.laetienda.user");
    	sc.setAttribute("emf", emf);
    	
    	setTomcatOnDb();
    	
    	log.debug("Contest Load Listener has started succesfully");
    }

	private void setTomcatOnDb() {
		UsuarioJndiRepoImpl urepo = new UsuarioJndiRepoImpl();
		try {
			urepo.setSettings(settings);
			urepo.setEntityManagerFactory(emf);
			urepo.setTomcatToDb();
		} catch (NamingException | GeneralSecurityException e) {
			// TODO String message =...
			log.debug(e);
		} 
	}
}
