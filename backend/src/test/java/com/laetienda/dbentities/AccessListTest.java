package com.laetienda.dbentities;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;
import org.laetienda.backend.engine.Ldap;

import com.laetienda.backend.install.InstallData;
import com.laetienda.backend.myapptools.Settings;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.lib.utilities.Aes;

class AccessListTest {
	private final static Logger log = LogManager.getLogger();
	
	private static EntityManagerFactory emf;
	private static AuthTables tables;
	private Db db;
	private Ldap ldap;
	private EntityManager em;
	private LdapConnection conn;
	private Authorization auth;
	private User aclUser, aclMember;
	private Group aclGroup;
	private TypedQuery<?> query;

	@BeforeAll
	static void setUpBeforeClass() {
		
		Ldap ldap = new Ldap();
		Db db = new Db();
		LdapConnection conn = null;
		emf = null;
		tables = new AuthTables();
		InstallData installer = new InstallData();
		EntityManager em = null;
		
		try {
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			installer.createObjects(em, conn, new Authorization(conn));
			Authorization.setACL_ALL_ID(em.createNamedQuery("AccessList.findByName", AccessListRepository.class).setParameter("name", "all").getSingleResult().getId());

		} catch (Exception e) {
			log.error("User Test failed.", e);
			fail("User Test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
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
	public void setConnection() {
		ldap = new Ldap();
		db = new Db();
		
		try {
			String password = new Aes().decrypt(Settings.TOMCAT_AES_PASS, "tomcat");
			conn = ldap.getLdapConnection(Settings.LDAP_TOMCAT_DN, password);
			em = emf.createEntityManager();
			query = em.createNamedQuery("AccessList.findByName", AccessListRepository.class).setParameter("name", "aclTest");
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	@AfterEach
	public void closeConnection() {
		db.closeEm(em);
		ldap.closeLdapConnection(conn);
	}

	@Test
	void testAclCycle() {
		createComponents();
		createAcl();
		modifyAcl();
		deleteAcl();
		removeComponents();
	}
	
	private void createAcl() {
		
		auth = new Authorization("aclUser", "passwd1234", tables);
		AccessListRepository aclTest = new AccessListRepository("aclTest", "Acl created for testing", aclUser, aclGroup, em, conn);
		
		try {
			assertNull(db.find(query, em, auth), "At this point the accesslist should not be created yet.");
			assertTrue(db.insert(aclTest, em, auth));
			db.commit(em, auth);
			assertNotNull(db.find(query, em, auth), "The acl should have been created.");
			assertTrue(aclTest.isAuthorized(aclUser, conn));
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void modifyAcl() {
		AccessListRepository acl = (AccessListRepository)db.find(query, em, auth);
		AccessListRepository acl2;
		try {
			db.begin(em);
			assertNotNull(acl, "It didn't find the access list");
			assertFalse(acl.isAuthorized(aclMember, conn), "Member must not be authorized yet.");
			acl.addUser(aclMember, conn);
			db.commit(em, auth);
			acl2 = (AccessListRepository)db.find(query, em, auth);
			assertTrue(acl2.isAuthorized(aclMember, conn), "Member was not added to the acl");
		}catch(Exception e) {
			myCatch(e);
		}
	}
	
	private void deleteAcl() {
		
		AccessListRepository acl = (AccessListRepository)db.find(query, em, auth);
		assertNotNull(acl, "Accesslist shoulbe be there before remove it");

		try {
			db.begin(em);
			assertTrue(db.remove(acl, em, auth));
			db.commit(em, auth);
			
			assertNull((AccessListRepository)db.find(query, em, auth));
		} catch (Exception e) {
			myCatch(e);
		} finally {
			db.closeEm(em);
		}
	}
	
	private void createComponents() {
		try {
			assertTrue(ldap.insertLdapEntity(new User("aclUser", "Acl User", "Sn Less", "acluser@mail.com", "passwd1234", "passwd1234", conn),  conn), "Acl User was not created correctly");
			assertTrue(ldap.insertLdapEntity(new User("aclMember", "Acl Member", "Sn Less", "aclmember@mail.com", "passwd1234", "passwd1234", conn),  conn), "Acl Member was not created correctly");
			aclUser = ldap.findUser("aclUser", conn);
			aclMember = ldap.findUser("aclMember", conn);
			assertTrue(ldap.insertLdapEntity(new Group("aclGroup", "Group for testing acls", aclUser, conn),  conn), "Acl Member was not created correctly");
			aclGroup = ldap.findGroup("aclGroup", conn);
		} catch (Exception e) {
			 myCatch(e);
		}
	}
	
	private void removeComponents() {
		try {
			conn.delete(aclGroup.getLdapEntry().getDn());
			conn.delete(aclUser.getLdapEntry().getDn());
			conn.delete(aclMember.getLdapEntry().getDn());
		} catch (LdapException e) {
			myCatch(e);
		}
	}
	
	private void myCatch(Exception e) {
		log.error("Group test failed.", e);
		fail("Group test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
}
