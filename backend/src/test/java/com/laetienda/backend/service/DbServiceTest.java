package com.laetienda.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;
import org.laetienda.backend.engine.Ldap;

import com.google.gson.Gson;
import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.backend.repository.ComponentRepository;
import com.laetienda.backend.test.AppContext;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Component;
import com.laetienda.lib.utilities.Aes;

class DbServiceTest {
	private final static Logger log = LogManager.getLogger(DbServiceTest.class);
	
	private static AppContext app;
	private AuthTables tables;
	private EntityManagerFactory emf;
	private Ldap ldap;
	private Db db;
	private EntityManager em;
	private Authorization authSysadmin;
	private DbService service;
	private Gson gson;
	private LdapConnection connTomcat;
	
	@BeforeAll
	static void init(){
		app = new AppContext();
		app.init();
	}
	
	@AfterAll
	static void destroy() {
		app.destroy();
	}
	
	@BeforeEach
	public void setConnection() {
		ldap = new Ldap();
		db = new Db();
		gson = new Gson();
		emf = (EntityManagerFactory)app.getAttribute("emf");
		tables = (AuthTables)app.getAttribute("tables");
		
		try {
			em = emf.createEntityManager();
			String password = new Aes().decrypt(Ajustes.SYSADMIN_AES_PASS, "sysadmin");
			authSysadmin = new Authorization("sysadmin", password, tables);
			password = new Aes().decrypt(Ajustes.TOMCAT_AES_PASS, "tomcat");
			connTomcat = ldap.getLdapConnection(Ajustes.LDAP_TOMCAT_DN, password);
			service = new DbService(emf, authSysadmin, connTomcat);
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	@AfterEach
	public void closeConnection() {
		db.closeEm(em);
		ldap.closeLdapConnection(connTomcat);
		ldap.closeAuthorization(authSysadmin);
	}

	@Test
	void testFind() {
		
	}
	
	@Test
	void testCycle() {
		testAdd();
		testPut();
		testDelete();
	}


	void testAdd() {
		
		EntityManager em = null;
		LdapConnection conn = authSysadmin.getLdapConnection();
		
		try {
			em = emf.createEntityManager();
			TypedQuery<?> query = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "all");
			User user = ldap.findUser("tomcat", conn);
			Group group = ldap.findGroup("managers", conn);
			
			AccessList aclAll = (AccessList)db.find(query, em, authSysadmin);
			AccessListRepository aclAllRepo = new AccessListRepository(aclAll);
			ComponentRepository compRepo = new ComponentRepository("wiki", "Wiki pages", com.laetienda.lib.model.Wiki.class, user, group, aclAllRepo, aclAllRepo, aclAllRepo, em, conn);
			Component comp = compRepo.getObjeto();
			log.debug(gson.toJson(comp));
			Component result = (Component)service.post(comp.getClass().getSimpleName(), gson.toJson(comp));
			assertNotNull(result);
		} catch (Exception e) {
			myCatch(e);
		} finally {
			db.closeEm(em);
		}
	}
	
	void testPut() {

		EntityManager em = null;
		Component result;
		try {
			em = emf.createEntityManager();
			TypedQuery<?> query = em.createNamedQuery("Component.findByName", Component.class).setParameter("name", "wiki");
			Component comp = (Component)db.find(query, em, authSysadmin);
			assertNotNull(comp);
			ComponentRepository compRepo = new ComponentRepository(comp);
			compRepo.setDescription("Another description");
			String json = gson.toJson(compRepo.getObjeto());
			log.debug("$json: {}", json);
			result = (Component)service.put(comp.getClass().getSimpleName(), json);
			
			assertNotNull(result, "It failed to update the object");
			assertTrue(result.getDescription().equals("Another description"), "It failed to merge changes");
		}catch(Exception e) {
			myCatch(e);
		}finally {
			db.closeEm(em);
		}
	}
	
	private void testDelete() {
		
	}
	
	private void myCatch(Exception e) {
		log.error("Group test failed.", e);
		fail("Group test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}

}
