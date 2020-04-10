package com.laetienda.dbentities;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.laetienda.install.InstallData;
import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;
import com.laetienda.mydatabase.Db;
import com.laetienda.myldap.Group;
import com.laetienda.myldap.Ldap;
import com.laetienda.myldap.User;

class AccessListTest {
	private final static Logger log = LogManager.getLogger();
	
	private static EntityManagerFactory emf;
	private static Db db;
	private static Ldap ldap;
//	private static EntityManager em;
	private static LdapConnection conn;

	@BeforeAll
	static void setUpBeforeClass() {
		InstallData installer = new InstallData();
		EntityManager em = null;
		try {
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			ldap = new Ldap();
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			db = new Db();
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			installer.createObjects(em, conn);
		} catch (Exception e) {
			myCatch(e);
		}finally {
			db.closeEm(em);
		}
	}
	
	@AfterAll
	static void setAfterClass() {
		ldap.closeLdapConnection(conn);
		db.closeEmf(emf);
	}

	@Test
	void testAclCycle() {
		createAcl();
		modifyAcl();
		deleteAcl();
	}
	
	private void createAcl() {
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			
			log.debug("$Number_of_acls: {}", em.createNamedQuery("AccessList.findall", AccessList.class).getResultList().size());
			assertEquals(0, em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "acltest").getResultList().size());
			User acltestuser = new User("acltestuser", "Acl", "Test User", "passwd", "passwd", "acltest@email.com", conn);
			ldap.insertLdapEntity(acltestuser, conn);
			Group acltestgroup = new Group("acltestgroup", "Group for testing ACLs.", acltestuser, conn);
			ldap.insertLdapEntity(acltestgroup, conn);
			AccessList acl = new AccessList("acltest", "This is a testing acl", acltestuser, acltestgroup, em, conn);
			db.insert(acl, em);
			db.commit(em);
			assertEquals(1, em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "acltest").getResultList().size());
		} catch (Exception e) {
			myCatch(e);
		} finally {
			db.closeEm(em);
		}
	}
	
	private void modifyAcl() {
		
	}
	
	private void deleteAcl() {
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			User user = new User("acltestuser", conn);
			Group group = new Group("acltestgroup", conn);
			conn.delete(user.getLdapEntry().getDn());
			conn.delete(group.getLdapEntry().getDn());
			assertEquals(1, em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "acltest").getResultList().size());
			AccessList acl = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "acltest").getSingleResult();
			em.remove(acl);
			db.commit(em);
			assertEquals(0, em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "acltest").getResultList().size());
			
		} catch (Exception e) {
			myCatch(e);
		} finally {
			db.closeEm(em);
		}
	}
	
	private static void myCatch(Exception e) {
		log.error("Group test failed.", e);
		fail("Group test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
}
