package com.laetienda.install;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.myapptools.MyAppTools;
import com.laetienda.myldap.Group;
import com.laetienda.myldap.Ldap;
import com.laetienda.myldap.User;

public class CreateGroups {
	private final static Logger log = LogManager.getLogger(CreateGroups.class);
	
	MyAppTools tools;
	Ldap ldap;
	
	public CreateGroups() {
		tools = new MyAppTools();
		ldap = new Ldap();
	}
	
	public Group createSysadmins(LdapConnection conn) throws Exception {
		log.info("Creating sysadmins group in ldap directory");
		
		User sysadmin = new User("sysadmin", conn);
		Group result = ldap.findGroup("sysadmins", conn);
		
		if(result == null) {
			result = new Group("sysadmins", "Group of sysadmins", sysadmin, conn);
			ldap.insertLdapEntity(result, conn);
		}else {
			log.info("Group already exists in ldap");
		}
		return result;
	}
	
	public Group createManagers(LdapConnection conn) throws Exception {
		log.info("Creating managers group in ldap directory");
		
		User manager = new User("manager", conn);
		Group result = ldap.findGroup("managers", conn);
		
		if(result == null) {
			result = new Group("managers", "Group of managers", manager, conn);
			ldap.insertLdapEntity(result, conn);
		}else {
			log.info("Group already exists in ldap");
		}
		return result;
	}

}
