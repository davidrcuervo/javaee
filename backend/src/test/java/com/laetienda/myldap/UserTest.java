package com.laetienda.myldap;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapNoPermissionException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laetienda.engine.Aes;
import org.laetienda.engine.Authorization;
import org.laetienda.engine.Db;
import org.laetienda.engine.Ldap;

import com.laetienda.install.InstallData;
import com.laetienda.myapptools.Settings;

class UserTest {
	private final static Logger log = LogManager.getLogger(UserTest.class);
	private static EntityManagerFactory emf;
	private Ldap ldap;
	private String password;
	private LdapConnection userConn;
	private LdapConnection tomcatConn;
	
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
			installer.createObjects(em, conn, new Authorization(conn));
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
		userConn = null;
		try {
			password = new Aes().decrypt(Settings.TOMCAT_AES_PASS, "tomcat");
			tomcatConn = ldap.getLdapConnection(Settings.LDAP_TOMCAT_DN, password);
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	@AfterEach
	public void closeConnection() {
		ldap.closeLdapConnection(tomcatConn);
	}
	
	@Test
	public void userCycle() {
		createUser();
		modifyUser();
		deleteUser();
	}
	
	private void createUser() {
		
		try {
			assertNull(ldap.findUser("testUser", tomcatConn), "At this point test user should not exist.");
			User user = new User("testuser", "Test", "Test", "test@email.com", "passwd1234", "passwd1234", tomcatConn);
			ldap.insertLdapEntity(user, tomcatConn);
			userConn = ldap.getLdapConnection("uid=testuser," + Settings.LDAP_PEOPLE_DN, "passwd1234");
			assertNotNull(ldap.findUser("testuser", tomcatConn));
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void modifyUser() {
				
		User user = ldap.findUser("testuser", tomcatConn);
	
		try {			
			assertEquals("test@email.com", user.getEmail(), "");
			user.setEmail("manager@la-etienda.com", tomcatConn);
			assertTrue(user.getErrors().size() > 0, "An error should have been reported because email is used by another user");
			ldap.modify(user, userConn);
			user = ldap.findUser("testuser", tomcatConn);
			assertEquals("test@email.com", user.getEmail(), "it should not have changed email address because email is used by another user");
			user.setEmail("address@email.com", tomcatConn);
			ldap.modify(user, userConn);
			User user2 = ldap.findUser("testuser", tomcatConn);
			assertEquals("address@email.com", user2.getEmail(), "user didn't modify. Another email address was expected");
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void deleteUser() {
		User user = ldap.findUser("testuser", tomcatConn);
		
		try {
			assertTrue(tomcatConn.exists(user.getLdapEntry().getDn()));
			assertThrows(LdapNoPermissionException.class, () -> {userConn.delete(user.getLdapEntry().getDn());});
			tomcatConn.delete(user.getLdapEntry().getDn());
			assertFalse(tomcatConn.exists(user.getLdapEntry().getDn()));
			assertNull(ldap.findUser("testuser", tomcatConn));
		} catch (LdapException e) {
			myCatch(e);
		}
	}
	
	private void myCatch(Exception e) {
		log.error("User Test failed.", e);
		fail("User Test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
}
