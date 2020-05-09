package com.laetienda.mydatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Ldap;

import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.utilities.Aes;

public class GroupTest {
	private static final Logger log = LogManager.getLogger();
	
	private Ldap ldap;
	
	public GroupTest() {
		ldap = new Ldap();
	}
	
	public Group findGroup(String group, LdapConnection conn) {
		
		Dn dn;
		Group result = null;
		try {
			dn = ldap.buildDn("cn=" + group + ", ou=groups, " + Ajustes.LDAP_DOMAIN);
			result = new Group(dn, conn);
			
			for(Attribute attr : result.getLdapEntry().getAttributes()) {
				log.debug("{} -> {}", attr.getId(), attr.get().getString());
			}
		} catch (LdapException | IOException e) {
			log.warn("Failed to find group. $groupName: {}", group);
			log.debug("Failed to find group. $groupName: {}", e);
		}
		
		return result;
	}
	
	public List<User> findUsers(String groupName, LdapConnection conn){
		
		List<User> result = new ArrayList<User>();
		
		try {
			Group group = new Group(groupName, conn);
			result = group.getMembers(conn);
			for(User user : result) {
				for(Attribute attr : user.getLdapEntry().getAttributes()) {
					log.debug("{} -> {}", attr.getId(), attr.get().getString());
				}
			}
		} catch (Exception e) {
			log.warn("Failed to find group users. $error: {}", e.getMessage());
			log.debug("Failed to find group users.", e);
		}
		
		return result;
	}
	
	public void add(LdapConnection conn) {
		log.info("Testing add group....");
		Group group = new Group();

		try {
			User tomcat = new User("tomcat", conn);
			group.setName("Test Group", conn);
			group.setDescription("Testing group");
			group.setOwner(tomcat, conn);
			group.addMember(tomcat, conn);
			ldap.insertLdapEntity(group, conn);
			log.info("Testing adding group has finishes succesfully");
		} catch (Exception e) {
			log.warn("Failed to test add group");
			log.debug("Failed to test add group", e);
		}
	}
	
	public void addUser(String groupName, String username, LdapConnection conn) {
		
		try {
			Group group = new Group(groupName, conn);
			User user = new User(username, conn);
			
			group.addMember(user, conn);
			ldap.modify(group, conn);
		} catch (Exception e) {
			log.warn("Failed to add user to group. $group: {} - $user: {}", groupName, username);
			log.debug("Failed to add user to group", e);
		}
	}
	
	public void removeUser(String groupName, String username, LdapConnection conn) throws Exception {
		Group group = new Group(groupName, conn);
		group.removeMember(username, conn);
		ldap.modify(group, conn);
		this.findUsers(groupName, conn);
	}
	
	public static void main(String[] args) {
		log.info("Testing GROUP LDAP Module...");
		
		GroupTest test = new GroupTest();
		Ldap ldap = new Ldap();
		LdapConnection conn = null;

		try {
			String password = new Aes().decrypt(Ajustes.LDAP_ADIN_AES_PASSWORD, Ajustes.LDAP_ADMIN_USER);
			conn=ldap.getLdapConnection(Ajustes.LDAP_ADMIN_USER, password);
			test.add(conn);
//			test.addUser("Test Group", "manager", conn);
//			test.findGroup("Test Group", conn);
			test.findUsers("Test Group", conn);
//			test.removeUser("Test Group", "manager", conn);
			test.removeUser("Test Group", "tomcat", conn);
		} catch (Exception e) {
			log.error("Test failed.", e);
		} finally {
			ldap.closeLdapConnection(conn);
		}
		
		log.info("Java has closed");
	}

}
