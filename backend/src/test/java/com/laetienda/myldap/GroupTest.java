package com.laetienda.myldap;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;

class GroupTest {
	private final static Logger log = LogManager.getLogger(Group.class);
	
	private static Ldap ldap;
	private static LdapConnection conn;
	
	@BeforeAll
	static void setUpBeforeClass() {
		try {
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			ldap = new Ldap();
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
		} catch (Exception e) {
			myCatch(e);
		}
	}

	@AfterAll
	static void setUpAfterClass() {
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
			assertNull(ldap.findGroup("testgroup", conn));
			User owner = ldap.findUser("sysadmin", conn);
			Group group = new Group("testgroup", "This is a testing group", owner, conn);
			ldap.insertLdapEntity(group, conn);
			assertNotNull(ldap.findGroup("testgroup", conn));
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
		try {
			User user = new User("manager", conn);
			Group group = new Group("testgroup", conn);
			assertFalse(group.isMember(user, conn));
			group.addMember(user, conn);
			ldap.modify(group, conn);
			Group grp2 = new Group("testgroup", conn);
			assertTrue(grp2.isMember(user, conn));
		} catch (Exception e) {
			myCatch(e);
		}
	}
	
	private void removeUser() {
		try {
			User user = new User("manager", conn);
			Group grp = new Group("testgroup", conn);
			assertTrue(grp.isMember(user, conn));
			grp.removeMember(user, conn);
			ldap.modify(grp, conn);
			assertFalse(grp.isMember(user, conn));
			Group grp2 = new Group("testgroup", conn);
			assertFalse(grp2.isMember(user, conn));
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
	
	private static void myCatch(Exception e) {
		log.error("Group test failed.", e);
		fail("Group test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}

}
