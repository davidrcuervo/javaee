package com.laetienda.myldap;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;

class UserTest {
	private final static Logger log = LogManager.getLogger(UserTest.class);
	
	private static Ldap ldap;
	private static String password; 
	private static LdapConnection conn;
	
	@BeforeAll
	public static void StartVars(){
		try {
			ldap = new Ldap();
			password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
		} catch (GeneralSecurityException e) {
			myCatch(e);
		}
	}
	
	@BeforeEach
	public void setConnections() {
		try {
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
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
		modifyUser();
		deleteUser();
	}
	
	private void createUser() {
		try {
			User user = new User("testuser", "Test", "Test", "passwd", "passwd", "test@email.com", conn);
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
	
	private static void myCatch(Exception e) {
		log.error("User Test failed.", e);
		fail("User Test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
}
