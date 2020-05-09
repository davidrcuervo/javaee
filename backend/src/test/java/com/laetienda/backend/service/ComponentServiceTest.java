package com.laetienda.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;

import javax.persistence.EntityManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Ldap;
import org.laetienda.backend.engine.Settings;

import com.google.gson.Gson;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.repository.ComponentRepository;
import com.laetienda.backend.test.AppContext;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Component;
import com.laetienda.lib.model.Wiki;
import com.laetienda.lib.utilities.Aes;

class ComponentServiceTest {
	private final static Logger log = LogManager.getLogger(ComponentService.class);
	
	
	private static AppContext app;
	private Aes aes;
	private Gson gson;
	private EntityManagerFactory emf;
	private AuthTables tables;
	private Settings settings;
	private Authorization sysadminAuth;
	private ComponentService service;
	private Ldap ldap;
	private AccessList aclAll;
	
	@BeforeAll
	public static void init() {
		app = new AppContext();
		app.init();
	}
	
	@AfterAll
	public static void destroy() {
		app.destroy();
	}
	
	@BeforeEach
	public void doBefore() {
		aes = new Aes();
		ldap = new Ldap();
		gson = new Gson();
		emf = (EntityManagerFactory)app.getAttribute("emf");
		settings = (Settings)app.getAttribute("settings");
		tables = (AuthTables)app.getAttribute("tables");
		
		String sysadminUsername = settings.get("sysadmin.username");
		
		try {
			String sysadminPassword = aes.decrypt(settings.get("sysadmin.aes.password"), sysadminUsername);
			sysadminAuth = new Authorization(sysadminUsername, sysadminPassword, tables);
			service = new ComponentService(emf, sysadminAuth);
			AccessListService aclService = new AccessListService(emf, sysadminAuth);
			aclAll = (AccessList) aclService.get("all").getObjeto();
		} catch (GeneralSecurityException e) {
			log.warn("Failed to start component test. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to start component test.", e);
		}
	}
	
	@AfterEach
	public void doFinally() {
		ldap.closeAuthorization(sysadminAuth);
		log.debug("Component test has finished");
	}

	@Test
	void doTest() {
		doPost();
		doGet();
		doPut();
		doDelete();
	}
	
	private void doGet() {
		ComponentRepository comR = (ComponentRepository)service.get("componentTest");
		assertNotNull(comR);
		log.debug("$jsonComponentGet: {}", gson.toJson(comR.getObjeto()));
	}
	
	private void doPost() {
		Component comp = new Component();
		comp.setName("componentTest");
		comp.setDescription("This component will test component service");
		comp.setOwner("tomcat");
		comp.setGroup("sysadmins");
		comp.setRead(aclAll);
		comp.setWrite(aclAll);
		comp.setDelete(aclAll);
		comp.setJavaClassName(Wiki.class.getName());
		
		ComponentRepository compR = (ComponentRepository) service.post(gson.toJson(comp));
		assertNotNull(compR);
		assertFalse(compR.getErrors().size() > 0);
		log.debug("$jsonComponentPost: {}", gson.toJson(compR.getObjeto()));
	}
	
	private void doPut() {
		Component comp = (Component) service.get("componentTest").getObjeto();
		AccessList aclSysadmin = (AccessList) new AccessListService(emf, sysadminAuth).get("sysadmin").getObjeto();
		assertFalse(comp.getDelete().getName().toLowerCase().equals("sysadmin"));
		assertFalse(comp.getWrite().getName().toLowerCase().equals("sysadmin"));
		comp.setDelete(aclSysadmin);
		comp.setWrite(aclSysadmin);
		comp.setDescription("Change description to test component put service");
		log.debug("$jsoncomponentbeforeput: {}", gson.toJson(comp));
		service.put(gson.toJson(comp));
		comp = (Component) service.get("componentTest").getObjeto();
		assertTrue(comp.getDelete().getName().toLowerCase().equals("sysadmin"));
		assertTrue(comp.getWrite().getName().toLowerCase().equals("sysadmin"));
		log.debug("$jsonComponentPut: {}", gson.toJson(comp));
	}
	
	public void doDelete() {
		Component comp = (Component) service.get("componentTest").getObjeto();
		assertNotNull(service.get("componentTest"));
		assertTrue(service.delete(comp.getName()));
		assertNull(service.get("componentTest"));
	}
}
