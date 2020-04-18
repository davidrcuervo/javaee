package com.laetienda.tomcat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.engine.Aes;
import org.laetienda.engine.Authorization;
import org.laetienda.engine.Db;
import org.laetienda.engine.Ldap;

import static com.laetienda.myapptools.Settings.*;

import com.laetienda.install.InstallData;
import com.laetienda.model.AccessList;
import com.laetienda.myauth.AuthTables;

/**
 * Application Lifecycle Listener implementation class Load
 *
 */

public class Load implements ServletContextListener {
	private static final Logger log = LogManager.getLogger();
	
	private Db db;
	private Ldap ldap;
	private AuthTables tables;
	private String password;
	private EntityManagerFactory emf;

    public Load() {
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
		//TODO finalize to copy all parameters from web.xml file
		
		InstallData installer = new InstallData();
		LdapConnection conn = null;
		EntityManager em = null;
		Authorization auth = null;
		TypedQuery<?> query;
		
		try {
			emf = db.createEntityManagerFactory();
			sc.setAttribute("emf", emf);
			sc.setAttribute("tables", tables);
			
			//for developing porposes the installer will run every time
			em = emf.createEntityManager();
			password = new Aes().decrypt(LDAP_ADIN_AES_PASSWORD, LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(LDAP_ADMIN_USER, password);
			auth = new Authorization(conn);
			installer.createObjects(em, conn, auth);
			
			log.debug("Setting id all acl");
			query = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "all");
			AccessList aclAll = (AccessList)db.find(query, em, auth);
			Authorization.setACL_ALL_ID(aclAll.getId());
			log.debug("ACL id for all has been set to authorization");
			
		} catch (Exception e) {
			log.fatal("Failed to initialize tomcat context.", e);
		} finally {
			ldap.closeLdapConnection(conn);
			db.closeEm(em);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		db.closeEmf(emf);
		log.info("Application context has closed succesfully");
	}
}
