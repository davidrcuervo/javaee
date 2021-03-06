package com.laetienda.mydatabase;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
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

class InstallTest {
	private final static Logger log = LogManager.getLogger(InstallTest.class);

	private static EntityManagerFactory emf;
	private InstallData installer;
	private Ldap ldap;
	private Db db;
	private Authorization auth;
	
	@BeforeAll
	static void initEmf() {
		Db db = new Db();
		emf = null;
		
		try {
			emf = db.createEntityManagerFactory();
		} catch (GeneralSecurityException e) {
			log.error("Failed to start databas connection (emf).", e);
			fail("Failed to start databas connection (emf).");
		}
	}
	
	@AfterAll
	static void closeEmf() {
		Db db = new Db();
		db.closeEmf(emf);
	}
	
	@BeforeEach
	public void init(){
		ldap = new Ldap();
		db = new Db();
		installer = new InstallData();
	}
	
	@Test
	void install() {
		
		EntityManager em =null;
		LdapConnection conn= null;
		
		try {
			em = emf.createEntityManager();
			String password = new AesFirstRepoImpl().decrypt(Ajustes.LDAP_ADIN_AES_PASSWORD, Ajustes.LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(Ajustes.LDAP_ADMIN_USER, password);
			auth = new Authorization(conn);
			installer.createObjects(em, conn, auth);
			testAcls(em, conn);
			
			
		}catch(Exception e) {
			fail("Critical exception caught. $exception: " + e.getClass().getSimpleName() + "-> " + e.getMessage());
			log.error("Test failed.", e);
		}finally {
			db.closeEm(em);
			db.closeEmf(emf);
			ldap.closeLdapConnection(conn);
		}
	}

	private void testAcls(EntityManager em, LdapConnection conn) throws Exception {
		
		int amountOfAcls = em.createNamedQuery("AccessList.findall", AccessListRepository.class).getResultList().size();
		assertEquals(6, amountOfAcls, "number of Access List. Check if all acls are created correctly");
	}
}
