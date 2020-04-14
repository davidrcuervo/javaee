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

import com.laetienda.dbentities.AccessList;
import com.laetienda.install.InstallData;
import com.laetienda.myauth.AuthTables;
import com.laetienda.myauth.Authorization;
import com.laetienda.mydatabase.Db;
import com.laetienda.myldap.Ldap;

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
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);

			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			installer.createObjects(em, conn);
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
			String password = new Aes().decrypt(Settings.TOMCAT_AES_PASS, "tomcat");
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
			String password = new Aes().decrypt(Settings.MANAGER_AES_PASS, "manager");
			Authorization auth = new Authorization("manager", password, tables);
			em = emf.createEntityManager();
			TypedQuery<?> query = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "manager");
			AccessList aclManager = (AccessList)db.find(query, em, auth);
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
