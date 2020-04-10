package com.laetienda.myapptools;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.laetienda.dbentities.AccessList;
import com.laetienda.install.InstallData;
import com.laetienda.myauth.Authorization;
import com.laetienda.mydatabase.Db;
import com.laetienda.myldap.Ldap;

class MyAppToolsTest {
	private final static Logger log = LogManager.getLogger();
	
	private static Ldap ldap;
	private static LdapConnection conn;
	private static Db db;
	private static EntityManagerFactory emf;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		EntityManager em = null;
		InstallData installer = new InstallData();
		try {
			ldap = new Ldap();
			db = new Db();
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			installer.createObjects(em, conn);
		}catch(Exception e) {
			myCatch(e);
		}
	}
	
	@AfterAll
	static void setAfterClass() {
		ldap.closeLdapConnection(conn);
		db.closeEmf(emf);
	}

	@Test
	void test() {
		authentication();
	}
	
	private void authentication() {
		
		EntityManager em = null;
		Authorization auth = new Authorization("manager", "manager");
		
		try {
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

	private static void myCatch(Exception e) {
		log.error("Group test failed.", e);
		fail("Group test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
}
