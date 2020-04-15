package com.laetienda.install;

import javax.persistence.EntityManager;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.laetienda.engine.Db;

import com.laetienda.model.AccessList;
import com.laetienda.myldap.Group;
import com.laetienda.myldap.User;

public class CreateAcls {
	
	Db db;
	User sysadmin, manager, tomcat;
	Group sysadmins, managers;
	AccessList aclSysadmin, aclAll;
	
	public CreateAcls(LdapConnection conn) throws Exception {
		db = new Db();

		sysadmin = new User("sysadmin", conn);
		manager = new User("manager", conn);
		tomcat = new User("tomcat", conn);
		
		sysadmins = new Group("sysadmins", conn);
		managers = new Group("managers", conn);
	}
	
	public AccessList createSysadminAcl(EntityManager em, LdapConnection conn) throws Exception {
		
		AccessList acl = new AccessList();
		acl.setName("sysadmin", em);
		acl.setDescription("Allow only sysadmin of the application");
		acl.setOwner(sysadmin, conn);
		acl.setGroup(sysadmins, conn);
		acl.setDelete(acl);
		acl.setWrite(acl);
		acl.setRead(acl);
		
		aclSysadmin = acl;
		
		return acl;
	}
	
	public AccessList createManagersAcl(EntityManager em, LdapConnection conn) throws Exception {
		AccessList acl = new AccessList();
		acl.setName("manager", em);
		acl.setDescription("Allow managers of the application");
		acl.setOwner(manager, conn);
		acl.setGroup(managers, conn);
		acl.setDelete(acl);
		acl.setWrite(acl);
		acl.setRead(acl);
		
		return acl;
	}
	
	public AccessList createAllAcl(EntityManager em, LdapConnection conn) {
		
		AccessList acl = new AccessList();
		acl.setName("all", em);
		acl.setDescription("Allow everybody to be granted");
		acl.setOwner(sysadmin, conn);
		acl.setGroup(sysadmins, conn);
		acl.setDelete(aclSysadmin);
		acl.setWrite(aclSysadmin);
		acl.setRead(acl);
		
		this.aclAll = acl;
		
		return acl;
	}
	
	public AccessList createOwnerAcl(EntityManager em, LdapConnection conn) throws Exception {
//		AccessList aclSysadmin = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "sysadmin").getSingleResult();
//		AccessList aclAll = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "all").getSingleResult();
		
		AccessList acl = new AccessList(
				"owner", "it includes only the owner of the object",
				sysadmin, sysadmins,
				aclSysadmin, aclSysadmin, aclAll,
				em, conn
				);
		
		return acl;
	}
	
	public AccessList createGroupAcl(EntityManager em, LdapConnection conn) throws Exception{
		
		AccessList acl = new AccessList(
				"group", "all members that belongs to group of the objeto",
				sysadmin, sysadmins,
				aclSysadmin, aclSysadmin, aclAll,
				em, conn
				);
		
		return acl;
	}
}
