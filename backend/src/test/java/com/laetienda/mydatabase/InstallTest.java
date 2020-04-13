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

import com.laetienda.dbentities.AccessList;
import com.laetienda.install.InstallData;
import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;
import com.laetienda.myldap.Ldap;

class InstallTest {
	private final static Logger log = LogManager.getLogger(InstallTest.class);

	private static EntityManagerFactory emf;
	private InstallData installer;
	private Ldap ldap;
	private Db db;
	
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
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			
			installer.createObjects(em, conn);
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
		
		int amountOfAcls = em.createNamedQuery("AccessList.findall", AccessList.class).getResultList().size();
		assertEquals(6, amountOfAcls, "number of Access List. Check if all acls are created correctly");
	}
}
