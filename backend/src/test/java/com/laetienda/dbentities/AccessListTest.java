package com.laetienda.dbentities;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.directory.api.ldap.model.exception.LdapException;
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

import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.backend.test.AppContext;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.utilities.Aes;

class AccessListTest {
	private final static Logger log = LogManager.getLogger();
	
	private static AppContext appCtx;
	private static EntityManagerFactory emf;
	private Db db;
	private Ldap ldap;
	private EntityManager em;
	private Authorization auth, aclMemberAuth, aclUserAuth;
	private User aclUser, aclMember;
	private Group aclGroup;
	private TypedQuery<?> query;

	@BeforeAll
	static void setUpBeforeClass() {
		appCtx = new AppContext();
		appCtx.init();
	}
	
	@AfterAll
	static void setAfterClass() {
		appCtx.destroy();
	}
	
	@BeforeEach
	public void setConnection() {
		ldap = new Ldap();
		db = new Db();
		emf = (EntityManagerFactory)appCtx.getAttribute("emf");
		try {

			em = emf.createEntityManager();
			query = em.createNamedQuery("AccessList.findByName", AccessListRepository.class).setParameter("name", "aclTest");
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	@AfterEach
	public void closeConnection() {
		db.closeEm(em);
		ldap.closeAuthorization(auth);
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
		
		AccessListRepository aclTest = null;
		
		try {
			aclTest = new AccessListRepository("aclTest", "Acl created for testing", aclUser, aclGroup, em, auth.getLdapConnection());
			assertNull(db.find(query, em, auth), "At this point the accesslist should not be created yet.");
			assertTrue(db.insert(aclTest.getObjeto(), em, auth));
			db.commit(em, auth);
			assertFalse(auth.canRead(aclTest.getObjeto()));
			assertTrue(aclUserAuth.canRead(aclTest.getObjeto()));
			assertNull(db.find(query, em, auth), "The acl should nto be retrieved due to not having permissions.");
			assertNotNull(db.find(query, em, aclUserAuth), "If acl exists and user has authorizations it should return the object.");
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void modifyAcl() {
		AccessList acl = (AccessList)db.find(query, em, aclUserAuth);
		AccessListRepository aclRepo = new AccessListRepository(acl);
		AccessList acl2;
		try {
			db.begin(em);
			assertNotNull(acl, "It didn't find the access list");
			assertFalse(aclMemberAuth.canRead(acl), "Member must not be authorized yet.");
			aclRepo.addUser(aclMember, auth.getLdapConnection());
			db.commit(em, aclUserAuth);
			acl2 = (AccessList)db.find(query, em, aclUserAuth);
			assertTrue(aclMemberAuth.canRead(acl2), "Member was not added to the acl");
		}catch(Exception e) {
			myCatch(e);
		}
	}
	
	private void deleteAcl() {
		
		AccessList acl = (AccessList)db.find(query, em, aclUserAuth);
		assertNotNull(acl, "Accesslist shoulbe be there before remove it");

		try {
			db.begin(em);
			assertTrue(db.remove(acl, em, aclUserAuth));
			db.commit(em, auth);
			assertNull((AccessList)db.find(query, em, aclUserAuth));
		} catch (Exception e) {
			myCatch(e);
		} 
	}
	
	private void createComponents() {
		
		try {
			String password = new Aes().decrypt(Ajustes.TOMCAT_AES_PASS, "tomcat");
			auth = new Authorization("tomcat", password, (AuthTables)appCtx.getAttribute("tables"));
			assertTrue(ldap.insertLdapEntity(new User("aclUser", "Acl User", "Sn Less", "acluser@mail.com", "passwd1234", "passwd1234", auth.getLdapConnection()),  auth.getLdapConnection()), "Acl User was not created correctly");
			assertTrue(ldap.insertLdapEntity(new User("aclMember", "Acl Member", "Sn Less", "aclmember@mail.com", "passwd1234", "passwd1234", auth.getLdapConnection()),  auth.getLdapConnection()), "Acl Member was not created correctly");
			aclUser = ldap.findUser("aclUser", auth.getLdapConnection());
			aclMember = ldap.findUser("aclMember", auth.getLdapConnection());
			assertTrue(ldap.insertLdapEntity(new Group("aclGroup", "Group for testing acls", aclUser, auth.getLdapConnection()),  auth.getLdapConnection()), "Acl Member was not created correctly");
			aclGroup = ldap.findGroup("aclGroup", auth.getLdapConnection());
			aclUserAuth = new Authorization("aclUser", "passwd1234", (AuthTables)appCtx.getAttribute("tables"));
			aclMemberAuth = new Authorization("aclMember", "passwd1234", (AuthTables)appCtx.getAttribute("tables"));
		} catch (Exception e) {
			 myCatch(e);
		}
	}
	
	private void removeComponents() {
		try {
			auth.getLdapConnection().delete(aclGroup.getLdapEntry().getDn());
			auth.getLdapConnection().delete(aclUser.getLdapEntry().getDn());
			auth.getLdapConnection().delete(aclMember.getLdapEntry().getDn());
		} catch (LdapException e) {
			myCatch(e);
		}finally {
			ldap.closeAuthorization(aclMemberAuth);
			ldap.closeAuthorization(aclUserAuth);
			ldap.closeAuthorization(auth);
		}
	}
	
	private void myCatch(Exception e) {
		log.error("Group test failed.", e);
		fail("Group test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
}
