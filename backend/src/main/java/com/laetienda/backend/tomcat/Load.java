package com.laetienda.backend.tomcat;

import static com.laetienda.backend.myapptools.Ajustes.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;
import org.laetienda.backend.engine.Ldap;
import org.laetienda.backend.engine.Settings;

import com.laetienda.backend.install.InstallData;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.utilities.Aes;

/**
 * Application Lifecycle Listener implementation class Load
 *
 */

public class Load implements ServletContextListener {
	private static final Logger log = LogManager.getLogger();
	
	private Db db;
	private Ldap ldap;
	private AuthTables tables;
	private EntityManagerFactory emf;
	private Settings settings;

    public Load() {
    	settings = new Settings();
        db = new Db();
        tables = new AuthTables();
        ldap = new Ldap();
    }

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Tomcat context is initializing");
		
		ServletContext sc = sce.getServletContext();
		
		DB_AES_PASSWORD = sc.getInitParameter("db.aes.password");
		DB_PORT = Integer.parseInt(sc.getInitParameter("db.port"));
		DB_USERNAME = sc.getInitParameter("db.username");
		HOSTNAME = sc.getInitParameter("hostname");
		
		/**
		//TODO finalize to copy all parameters from web.xml file
		**/
		InstallData installer = new InstallData();
		EntityManager em = null;
		Authorization authTomcat = null;
		Authorization authSysadmin = null;
		TypedQuery<?> query;
		String passTomcat, passSysadmin;
		
		try {
			emf = db.createEntityManagerFactory();
			sc.setAttribute("emf", emf);
			sc.setAttribute("tables", tables);
			sc.setAttribute("settings", settings);
			
			//for developing porposes the installer will run every time
			em = emf.createEntityManager();
			passTomcat = new Aes().decrypt(LDAP_ADIN_AES_PASSWORD, LDAP_ADMIN_USER);
			authTomcat = new Authorization(new Dn(LDAP_ADMIN_USER), passTomcat, tables);
			passSysadmin = new Aes().decrypt(SYSADMIN_AES_PASS, "sysadmin");
			authSysadmin = new Authorization("sysadmin", passSysadmin, tables);
			
			installer.createLdapObjects(authTomcat);
			installer.createDbObjects(em, authSysadmin);
			
			log.debug("Setting id all acl");
			query = em.createNamedQuery("AccessList.findByName", AccessListRepository.class).setParameter("name", "all");
			AccessList aclAll = (AccessList)db.find(query, em, authSysadmin);
			int aclAllid  = aclAll.getId();
			Authorization.setACL_ALL_ID(aclAllid);
			log.debug("ACL id for all has been set to authorization");
			
		} catch (Exception e) {
			log.fatal("Failed to initialize tomcat context.", e);
		} finally {
			ldap.closeAuthorization(authTomcat);
			ldap.closeAuthorization(authSysadmin);
			db.closeEm(em);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		db.closeEmf(emf);
		log.info("Application context has closed succesfully");
	}
}
