package com.laetienda.myapptools;

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

import com.laetienda.backend.install.InstallData;
import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.lib.utilities.AesFirstRepoImpl;

class MyAppToolsTest {
	private final static Logger log = LogManager.getLogger();
	
	private Ldap ldap;
	private Db db;
	private LdapConnection conn;
	private EntityManager em;
	private static EntityManagerFactory emf;
	private static AuthTables tables;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		tables = new AuthTables();
		EntityManager em = null;
		InstallData installer = new InstallData();
		Ldap ldap = new Ldap();
		Db db = new Db();
		LdapConnection conn = null;
		
		try {
			String password = new AesFirstRepoImpl().decrypt(Ajustes.LDAP_ADIN_AES_PASSWORD, Ajustes.LDAP_ADMIN_USER);

			conn = ldap.getLdapConnection(Ajustes.LDAP_ADMIN_USER, password);
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			installer.createObjects(em, conn, new Authorization(conn));
		}catch(Exception e) {
			fail("Failed to start test. $exeption: "  + e.getClass().getSimpleName() + " -> " + e.getMessage());
			log.error("Failed to start test.", e);
		}finally {
			ldap.closeLdapConnection(conn);
			db.closeEm(em);
		}
	}
	
	@AfterAll
	static void setAfterClass() {
		Db db = new Db();
		db.closeEmf(emf);
	}
	
	@BeforeEach
	public void initTest() {
		ldap = new Ldap();
		db = new Db();
		try {
			String password = new AesFirstRepoImpl().decrypt(Ajustes.TOMCAT_AES_PASS, "tomcat");
			em = emf.createEntityManager();
			conn = ldap.getLdapConnection("tomcat", password);
		} catch (Exception e) {
			myCatch(e);;
		}
	}
	
	@AfterEach
	public void closeConnections() {
		ldap.closeLdapConnection(conn);
		db.closeEm(em);
	}
	
	@Test
	public void authentication() {
		
		EntityManager em = null;
		
		try {
			String password = new AesFirstRepoImpl().decrypt(Ajustes.MANAGER_AES_PASS, "manager");
			Authorization auth = new Authorization("manager", password, tables);
			em = emf.createEntityManager();
			TypedQuery<?> query = em.createNamedQuery("AccessList.findByName", AccessListRepository.class).setParameter("name", "manager");
			AccessListRepository aclManager = (AccessListRepository)db.find(query, em, auth);
			assertNotNull(aclManager);
		}catch (Exception e) {
			myCatch(e);
		}finally {
			db.closeEm(em);
		}
	}

	private void myCatch(Exception e) {
		log.error("Application Tools test failed.", e);
		fail("Application Tools test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
}
