package com.laetienda.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult.Status;

import com.laetienda.model.webdb.Usuario;
import com.laetienda.user.lib.Settings;

class UsuarioJndiRepoImplTest {
	final static private Logger log = LogManager.getLogger(UsuarioJndiRepoImplTest.class);
	
	private UsuarioRepository urepo;
	private static Settings settings;
	private static EntityManagerFactory emf;
	
	@BeforeAll
	public static void load() {
		emf = Persistence.createEntityManagerFactory("com.laetienda.user");
		settings = new Settings();
		
		UsuarioJndiRepoImpl usrrepo = null;
		try {
			usrrepo = new UsuarioJndiRepoImpl(emf, settings, "tomcat");
			usrrepo.setTomcatToDb();
		}catch(GeneralSecurityException | NamingException e) {
			String message = String.format("Exception while setting tomcat user. $exceptions: %s, $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			fail(message);
		}finally {
			usrrepo.close();
		}
	}
	
	@BeforeEach
	public void init() {
		
		try {
			urepo = new UsuarioJndiRepoImpl(emf, settings, "tomcat");
		} catch (GeneralSecurityException | NamingException e) {
			log.debug(e);
		}
	}
	
	@AfterEach
	public void destroy() {
		urepo.close();
	}
	
	@AfterAll
	public static void end() {
		emf.close();
	}


	void testFindAll() {
		fail("Not yet implemented");
	}


	void testFindByUsername() {
		fail("Not yet implemented");
	}


	void testFindByEmail() {
		fail("Not yet implemented");
	}

	@Test
	void testInsert() throws Exception{
		assertNull(urepo.findByUsername("username"));
		assertNull(urepo.findByEmail("email.address@domain.com.co"));
		
		Usuario user = new Usuario("username", "First Name", "Last Name", "email.address@domain.com.co", "Clave1234");
		urepo.insert(user);
		Integer uid = user.getUid();
		
		Usuario test1 = urepo.findByUsername("username");
		assertNull(test1);
		
		UsuarioRepository urepo2 = new UsuarioJndiRepoImpl(emf, settings, "username");
		Usuario test2 = urepo2.findByUsername("username");
		assertEquals("email.address@domain.com.co", test2.getMail());
		assertEquals(uid, test2.getUid());
		
		Usuario test3 = urepo2.findByEmail("email.address@domain.com.co");
		assertEquals("username", test3.getUsername());
		assertEquals(uid, test3.getUid());
		
		assertEquals(com.laetienda.lib.usuario.Status.EMAIL_PENDING_CONFIRMATION, user.getStatus());
		urepo.enable(user);
		assertEquals(com.laetienda.lib.usuario.Status.ENABLED, user.getStatus());
		
		
		urepo2.delete(user);
		Usuario delete = urepo.findByUsername("username");
		assertNull(delete);
		
		urepo2.close();
	}

	void testUpdate() {
		fail("Not yet implemented");
	}

	void testDelete() {
		fail("Not yet implemented");
	}

	void testDisable() {
		fail("Not yet implemented");
	}

	void testEnable() {
		fail("Not yet implemented");
	}
}
