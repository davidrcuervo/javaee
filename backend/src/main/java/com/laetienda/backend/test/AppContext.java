package com.laetienda.backend.test;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;
import org.laetienda.backend.engine.Ldap;
import org.laetienda.backend.engine.Settings;

import com.laetienda.backend.install.InstallData;
import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.utilities.AesFirstRepoImpl;

public class AppContext {
	private final static Logger log = LogManager.getLogger(AppContext.class);

	private Map<String, Object> attributes;
	private Ldap ldap;
	private Db db;
	private EntityManagerFactory emf;
	private AuthTables tables;
	private Authorization authLdapAdmin, authSysadmin;
	private EntityManager em = null;
	private LdapConnection ldapConnectionTomcat;
	private Settings settings;
	
	public AppContext() {
		attributes = new HashMap<String, Object>();
		db = new Db();
		ldap = new Ldap();
		settings = new Settings();
	}
	
	public void init() {
		try {
			setAttribute("tables", setTables());
			setAttribute("emf", setEmf());
			authLdapAdmin = setLdapAdminAuthorization();
			em = emf.createEntityManager();
			installUsersAndGroups();
			authSysadmin = setSysadminAuthorization();
			installDatabase();
			setAllAcl();
			setAttribute("settings", settings);
			setAttribute("ldapConnectionTomcat", setTomcatLdapConnection());
		}catch(Exception e) {
			log.fatal("Failed to initialize tomcat context.", e);
		}finally {
			ldap.closeAuthorization(authLdapAdmin);
			ldap.closeAuthorization(authSysadmin);
			db.closeEm(em);
		}
	}
	


	private Authorization setLdapAdminAuthorization() throws Exception {
		log.info("Building authorization from AppContext test class");
		String password;
		Authorization auth;
		
		password = new AesFirstRepoImpl().decrypt(Ajustes.LDAP_ADIN_AES_PASSWORD, Ajustes.LDAP_ADMIN_USER);
		Dn admindn = new Dn(Ajustes.LDAP_ADMIN_USER);
		log.info("Admin DN created succesfully. $dn: {}", admindn.getName());
		auth = new Authorization(admindn, password, tables);
		log.info("Authorization created succesfully. $user: {}", auth.getUser().getLdapEntry().getDn().getName());
		
		return auth;
	}
	
	private Authorization setSysadminAuthorization() throws Exception {
		String password;
		Authorization auth;
		
		password = new AesFirstRepoImpl().decrypt(Ajustes.SYSADMIN_AES_PASS, "sysadmin");
		auth = new Authorization("sysadmin", password, tables);
				
		return auth;
	}
	
	private void installUsersAndGroups() throws Exception {
		InstallData installer = new InstallData();
		installer.createLdapObjects(authLdapAdmin);
	}

	private void installDatabase() throws Exception {		
		InstallData installer = new InstallData();
		installer.createDbObjects(em, authSysadmin);
	}
	
	private void setAllAcl() {
		TypedQuery<?> query = em.createNamedQuery("AccessList.findByName", AccessListRepository.class).setParameter("name", "all");
		AccessList aclAll = (AccessList)db.find(query, em, authSysadmin);
		Authorization.setACL_ALL_ID(aclAll.getId());
	}
	
	private LdapConnection setTomcatLdapConnection() throws Exception {
		String password = new AesFirstRepoImpl().decrypt(Ajustes.TOMCAT_AES_PASS, "tomcat");
		return ldap.getLdapConnection(Ajustes.LDAP_TOMCAT_DN, password);
		
	}
	
	public void destroy() {
		db.closeEmf(emf);
		ldap.closeLdapConnection(ldapConnectionTomcat);
		log.info("Application context has closed succesfully");
	}

	private Object setTables() {
		tables = new AuthTables();
		return tables;
	}

	private EntityManagerFactory setEmf() throws GeneralSecurityException {
		emf = db.createEntityManagerFactory();
		return emf;
	}
	
	public void setAttribute(String key, Object object) {
		attributes.put(key, object);
	}
	
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
}
