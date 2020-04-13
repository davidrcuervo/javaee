package com.laetienda.myldap;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.laetienda.install.InstallData;
import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;
import com.laetienda.mydatabase.Db;

class UserTest {
	private final static Logger log = LogManager.getLogger(UserTest.class);
	private static EntityManagerFactory emf;
	private Ldap ldap;
	private String password;
	private LdapConnection conn;
	
	@BeforeAll
	public static void StartVars(){
		
		Ldap ldap = new Ldap();
		Db db = new Db();
		LdapConnection conn = null;
		emf = null;
		EntityManager em = null;
		InstallData installer = new InstallData();
		
		try {
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			installer.createObjects(em, conn);
		} catch (Exception e) {
			log.error("User Test failed.", e);
			fail("User Test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
		} finally {
			ldap.closeLdapConnection(conn);
			db.closeEm(em);
		}
	}
	
	@AfterAll
	public static void close() {
		Db db = new Db();
		db.closeEmf(emf);
	}
	
	@BeforeEach
	public void setConnection() {
		ldap = new Ldap();

		try {
			password = new Aes().decrypt(Settings.TOMCAT_AES_PASS, "tomcat");
			conn = ldap.getLdapConnection("uid=tomcat,ou=People," + Settings.LDAP_DOMAIN, password);
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	@AfterEach
	public void closeConnection() {
		ldap.closeLdapConnection(conn);
	}
	
	@Test
	public void userCycle() {
		createUser();
//		modifyUser();
//		deleteUser();
	}
	
	private void createUser() {
		
		try {
			User user = new User("testuser", "Test", "Test", "test@email.com", "passwd1234", "passwd1234", conn);
			ldap.insertLdapEntity(user, conn);
			assertNotNull(ldap.findUser("testuser", conn));
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void modifyUser() {
				
		User user = ldap.findUser("testuser", conn);

		try {			
			user.setEmail("address@email.com", conn);
			ldap.modify(user, conn);
			
			User user2 = ldap.findUser("testuser", conn);
			assertEquals("address@email.com", user2.getEmail(), "user didn't modify. Another email address was expected");
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void deleteUser() {
		User user = ldap.findUser("testuser", conn);
		
		try {
			assertTrue(conn.exists(user.getLdapEntry().getDn()));
			conn.delete(user.getLdapEntry().getDn());
			assertFalse(conn.exists(user.getLdapEntry().getDn()));
			assertNull(ldap.findUser("testuser", conn));
		} catch (LdapException e) {
			myCatch(e);
		}
	}
	
	private void myCatch(Exception e) {
		log.error("User Test failed.", e);
		fail("User Test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
}
