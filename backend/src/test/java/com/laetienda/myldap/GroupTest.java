package com.laetienda.myldap;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.utilities.Aes;

class GroupTest {
	private final static Logger log = LogManager.getLogger(Group.class);
	
	private static EntityManagerFactory emf;
	private Ldap ldap;
	private LdapConnection conn;
	private String password;
	private User owner, member1, member2;
	private Group testGrp;
	
	@BeforeAll
	static void setUpBeforeClass() {
		
		Ldap ldap = new Ldap();
		Db db = new Db();
		LdapConnection conn = null;
		emf = null;
		EntityManager em = null;
		InstallData installer = new InstallData();
		
		try {
			String password = new Aes().decrypt(Ajustes.LDAP_ADIN_AES_PASSWORD, Ajustes.LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(Ajustes.LDAP_ADMIN_USER, password);
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			installer.createObjects(em, conn, new Authorization(conn));
		} catch (Exception e) {
			log.error("User Test failed.", e);
			fail("User Test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
		}finally {
			ldap.closeLdapConnection(conn);
			db.closeEm(em);
		}
	}

	@AfterAll
	static void setUpAfterClass() {
		Db db = new Db();
		db.closeEmf(emf);
	}
	
	@BeforeEach
	public void setConnection() {
		ldap = new Ldap();

		try {
			password = new Aes().decrypt(Ajustes.TOMCAT_AES_PASS, "tomcat");
			conn = ldap.getLdapConnection(Ajustes.LDAP_TOMCAT_DN, password);
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	@AfterEach
	public void closeConnection() {
		ldap.closeLdapConnection(conn);
	}
	
	@Test
	void testGroupCycle() {
		createGroup();
		modifyGroup();
		addUser();
		removeUser();
		deleteGroup();
	}
	
	private void createGroup() {
		try {
			ldap.insertLdapEntity(new User("owner", "Owner", "SnLess", "owner@mail.com", "passwd1234", "passwd1234", conn), conn);
			ldap.insertLdapEntity(new User("member1", "Member", "SnLess", "member@mail.com", "passwd1234", "passwd1234", conn), conn);
			ldap.insertLdapEntity(new User("member2", "Member", "SnLess", "miembro@mail.com", "passwd1234", "passwd1234", conn), conn);
			owner = ldap.findUser("owner", conn);
			member1 = ldap.findUser("member1", conn);
			member2 = ldap.findUser("member2", conn);
			assertNull(ldap.findGroup("testgroup", conn), "Test is starting testgroup is not expected to exist");
			ldap.insertLdapEntity(new Group("testgroup", "This is a testing group", owner, conn), conn);
			testGrp = ldap.findGroup("testgroup", conn);
			assertNotNull(testGrp, "Group was not find, it was not persisted in directory");
			assertEquals(owner.getLdapEntry().getDn().getName(), testGrp.getLdapEntry().get("owner").get().getString());
			
			Group group2 = new Group("testgroup", "This is a testing group", owner, conn);
			assertTrue(group2.getErrors().size() > 0, "The same group name is not detected");
			assertFalse(ldap.insertLdapEntity(group2, conn), "A group with same name can't be added");
			
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void modifyGroup() {
		Group group = ldap.findGroup("testgroup", conn);
		try {
			assertEquals("This is a testing group", group.getLdapEntry().get("description").get().getString());
			group.setDescription("This a modification of the description");
			ldap.modify(group, conn);
			Group grp2 = ldap.findGroup("testgroup", conn);
			assertEquals("This a modification of the description", grp2.getLdapEntry().get("description").get().getString());
			
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void addUser() {
		Group group = ldap.findGroup("testgroup", conn);
		try {
			assertFalse(group.isMember(member1, conn));
			group.addMember(member1, conn);
			ldap.modify(group, conn);
			group = ldap.findGroup("testgroup", conn);
			assertTrue(group.isMember(member1, conn));
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void removeUser() {
		Group group = ldap.findGroup("testgroup", conn);
		try {
			assertTrue(group.isMember(member1, conn));
			group.removeMember(member1, conn);
			ldap.modify(group, conn);
			assertFalse(group.isMember(member1, conn));
			conn.delete(member1.getLdapEntry().getDn());
			conn.delete(member2.getLdapEntry().getDn());
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void deleteGroup() {
		Group group = ldap.findGroup("testgroup", conn);
		assertNotNull(group);
		try {
			conn.delete(group.getLdapEntry().getDn());
			assertNull(ldap.findGroup("testgroup", conn));
		} catch (LdapException e) {
			myCatch(e);
		}
	}
	
	private void myCatch(Exception e) {
		log.error("Group test failed.", e);
		fail("Group test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}

}
