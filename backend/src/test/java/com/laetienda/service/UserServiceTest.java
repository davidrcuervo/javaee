package com.laetienda.service;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.laetienda.backend.myapptools.Settings;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.service.UserService;
import com.laetienda.lib.utilities.Aes;

class UserServiceTest {
	
	private String password;
	private UserService service;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}
	
	@BeforeEach
	public void setConnection() {
		service = new UserService();
		
		try {
			password = new Aes().decrypt(Settings.TOMCAT_AES_PASS, "tomcat");
		} catch (GeneralSecurityException e) {
			fail("Failed to decrypt tomcat password.");
		}
	}

	@Test
	void test() {
		createUser();
		deleteUser();
	}

	private void deleteUser() {
		User user = service.findByUsername("tomcat", password, "testuser");
		assertNotNull(user);		
		assertTrue(service.delete("tomcat", password, "testuser"));
		assertNull(service.findByUsername("tomcat", password, "testuser"));
		
	}

	private void createUser() {
		assertNull(service.findByUsername(Settings.LDAP_TOMCAT_DN, password, "testuser"));
		assertNotNull(service.add(Settings.LDAP_TOMCAT_DN, password, "testuser", "Test User", "SnLess", "test@mail.com", "passwd1234", "passwd1234"));
		assertNotNull(service.findByUsername(Settings.LDAP_TOMCAT_DN, password, "testuser"));
	}

}
