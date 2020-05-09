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
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.backend.test.AppContext;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.utilities.Aes;

class AccessListServiceTest {
	private final static Logger log = LogManager.getLogger(AccessListRepository.class);
	
	private static AppContext app;
	private Aes aes;
	private Gson gson;
	private EntityManagerFactory emf;
	private AuthTables tables;
	private Settings settings;
	private Authorization sysadminAuth;
	private AccessListService service;
	private Ldap ldap;
	
	@BeforeAll
	static void init() {
		app = new AppContext();
		app.init();		
	}
	
	@AfterAll
	static void destroy() {
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
			service = new AccessListService(emf, sysadminAuth);
		} catch (GeneralSecurityException e) {
			doCatch(e);
		} 
	}
	
	@AfterEach
	public void doFinally() {
		ldap.closeAuthorization(sysadminAuth);
		log.debug("AccessList cycle test has finished succesfully");
	}
	
	@Test
	public void doTest() {
		doPost();
		doGet();
		doPut();
		doRemoveUser();
		doDelete();
	}
	
	public void doGet() {
		AccessListRepository aclR = (AccessListRepository)service.get("aclTest");
		assertNotNull(aclR);
		log.debug("$aclReadName: {}" ,aclR.getObjeto().getRead().getName());
		log.debug("$jsonAcl: {}", gson.toJson(aclR.getObjeto()));
	}

	
	public void doPost() {
		
		AccessList acl = new AccessList();
		acl.setOwner("tomcat");
		acl.setGroup("sysadmins");
		acl.setName("aclTest");
		acl.setDescription("Testing acl. It will test AccessListService");
		acl.addUser("tomcat");
		acl.addGroup("sysadmins");
		acl.setRead(acl);
		acl.setDelete(acl);
		acl.setWrite(acl);
		
		AccessListRepository aclR = (AccessListRepository) service.post(gson.toJson(acl));
		assertNotNull(aclR);
		assertFalse(aclR.getErrors().size() > 0);
		log.debug("$aclResult: {}", gson.toJson(aclR.getObjeto()));
	}

	
	void doPut() {
		AccessList aclAll = (AccessList) service.get("all").getObjeto();
		AccessList aclTest = (AccessList) service.get("aclTest").getObjeto();
		assertFalse(aclTest.getUsers().contains("manager"));
		assertFalse(aclTest.getGroups().contains("managers"));
		aclTest.addUser("manager");
		aclTest.addGroup("managers");
		aclTest.setRead(aclAll);
		
		AccessListRepository aclTestR = (AccessListRepository) service.put(gson.toJson(aclTest));
		assertNotNull(aclTestR);
		assertFalse(aclTestR.getErrors().size() > 0);
		assertTrue(aclTestR.getObjeto().getUsers().contains("manager"));
		assertTrue(aclTestR.getObjeto().getGroups().contains("managers"));
		log.debug("aclPut: {}", gson.toJson(aclTestR.getObjeto()));
	}
	
	public void doRemoveUser() {
		AccessList aclTest = (AccessList) service.get("aclTest").getObjeto();
		assertTrue(aclTest.getUsers().contains("manager"));
		assertTrue(aclTest.getGroups().contains("managers"));
		aclTest.getUsers().remove("manager");
		aclTest.getGroups().remove("managers");
		
		service.put(gson.toJson(aclTest));
		aclTest = (AccessList) service.get("aclTest").getObjeto();
		assertFalse(aclTest.getUsers().contains("manager"));
		assertFalse(aclTest.getGroups().contains("managers"));
		log.debug("$aclRemoveUser: {}", gson.toJson(aclTest));
	}

	void doDelete() {
		AccessList aclTest = (AccessList) service.get("aclTest").getObjeto();
		assertNotNull(aclTest);
		assertTrue(service.delete(aclTest.getName()));
		assertNull(service.get("aclTest"));
	}
	
	public void doCatch(Exception e) {
		log.debug("Test to Access List Service has failed.", e);
		fail("Test to Access List Service has failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}	
}
