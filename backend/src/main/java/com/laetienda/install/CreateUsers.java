package com.laetienda.install;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;
import com.laetienda.myldap.Ldap;
import com.laetienda.myldap.User;

public class CreateUsers {
	private final static Logger log = LogManager.getLogger(CreateUsers.class);
	
	Ldap ldap;
	
	public CreateUsers() {
		ldap = new Ldap();
	}
	
	public User createSysadmin(LdapConnection conn) throws Exception {
		log.info("Creating sysadmin user in ldap directory");
		
		User result = ldap.findUser("sysadmin", conn);
		if(result == null) {
			String password = new Aes().decrypt(Settings.SYSADMIN_AES_PASS, "sysadmin");
			result = new User("sysadmin", "SysAdmin", "SnLess", "sysadmin@la-etienda.com", password, password,  conn);
			ldap.insertLdapEntity(result, conn);
			
			log.info("Sysadmin user has been created succesfully in ldap.");
		}else {
			log.info("User already exists in ldap.");
		}
		
		return result;
	}
	
	public User createManager(LdapConnection conn) throws Exception {
		log.info("Creating Manager user in ldap directory");
		
		User result = ldap.findUser("manager", conn);
		if(result == null) {
			String password = new Aes().decrypt(Settings.MANAGER_AES_PASS, "manager");
			result = new User("manager", "Manager", "SnLess", "manager@la-etienda.com", password, password, conn);
			ldap.insertLdapEntity(result, conn);
			
			log.info("Manager user has been created succesfully in ldap.");
		}else {
			log.info("Manager already exists in ldap.");
		}
		
		return result;
	}
	
	public User createTomcat(LdapConnection conn) throws Exception {
		log.info("Creating Tomcat user in ldap directory");
		
		User result = ldap.findUser("tomcat", conn);
		if(result == null) {
			
			String password = new Aes().decrypt(Settings.TOMCAT_AES_PASS, "tomcat");
			result = new User("tomcat", "Tomcat", "SnLess", "tomcat@la-etienda.com", password, password,  conn);
			ldap.insertLdapEntity(result, conn);
			
			log.info("Tomcat user has been created succesfully in ldap.");
		}else {
			log.info("Tomcat already exists in ldap.");
		}
		
		return result;
	}
}
