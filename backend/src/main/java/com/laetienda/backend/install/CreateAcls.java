package com.laetienda.backend.install;

import javax.persistence.EntityManager;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.laetienda.backend.engine.Db;

import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.AccessListRepository;

public class CreateAcls {
	
	Db db;
	User sysadmin, manager, tomcat;
	Group sysadmins, managers;
	AccessListRepository aclSysadmin, aclAll;
	
	public CreateAcls(LdapConnection conn) throws Exception {
		db = new Db();

		sysadmin = new User("sysadmin", conn);
		manager = new User("manager", conn);
		tomcat = new User("tomcat", conn);
		
		sysadmins = new Group("sysadmins", conn);
		managers = new Group("managers", conn);
	}
	
	public AccessListRepository createSysadminAcl(EntityManager em, LdapConnection conn) throws Exception {
		
		aclSysadmin = new AccessListRepository(
				"sysadmin", "Allow only sysadmin of the application", sysadmin, sysadmins, em, conn
				);
	
		return aclSysadmin;
	}
	
	public AccessListRepository createManagersAcl(EntityManager em, LdapConnection conn) throws Exception {
		AccessListRepository acl = new AccessListRepository(
				"manager", "Allow managers of the application", manager, managers, em, conn
				);

		return acl;
	}
	
	public AccessListRepository createAllAcl(EntityManager em, LdapConnection conn) {
		
		aclAll = new AccessListRepository(
				"all", "Allow everybody to be granted", sysadmin, sysadmins, em, conn
				);
		aclAll.setDelete(aclSysadmin);
		aclAll.setWrite(aclSysadmin);
		aclAll.setRead(aclAll);
		return aclAll;
	}
	
	public AccessListRepository createOwnerAcl(EntityManager em, LdapConnection conn) throws Exception {
//		AccessList aclSysadmin = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "sysadmin").getSingleResult();
//		AccessList aclAll = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "all").getSingleResult();
		
		AccessListRepository acl = new AccessListRepository(
				"owner", "it includes only the owner of the object",
				sysadmin, sysadmins,
				aclSysadmin, aclSysadmin, aclAll,
				em, conn
				);
		
		return acl;
	}
	
	public AccessListRepository createGroupAcl(EntityManager em, LdapConnection conn) throws Exception{
		
		AccessListRepository acl = new AccessListRepository(
				"group", "all members that belongs to group of the objeto",
				sysadmin, sysadmins,
				aclSysadmin, aclSysadmin, aclAll,
				em, conn
				);
		
		return acl;
	}
}
