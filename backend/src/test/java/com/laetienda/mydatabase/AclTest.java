package com.laetienda.mydatabase;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.dbentities.AccessList;
import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;
import com.laetienda.myldap.Ldap;

public class AclTest {
	private final static Logger log = LogManager.getLogger(AclTest.class);
	
	
	
	public void createGroupAcl(EntityManager em, LdapConnection conn){
		log.info("Testing ACL create....");
		
		Db db = new Db();
		AccessList acl = new AccessList();

		try {		
			acl.setOwner("tomcat", conn);
			acl.setGroup("Test Group", conn);
			acl.addGroup("Test Group", conn);
			acl.addUser("tomcat", conn);
			acl.setName("test acl", em);
			acl.setDescription("Includes sysadmin user and sysadmin group");
			db.insert(acl, em);
			db.commit(em);
		
			log.info("...creating ACL has finished succesfully");
		} catch (Exception e) {
			log.warn("Failed to create ACL $exception: {}", e.getMessage());
			log.debug("Failed to create ACL.", e);
		}
	}
	
	public static void main(String[] args) {
		log.info("Testin ACLs...");
		AclTest test = new AclTest();
		Db db = new Db();
		Ldap ldap = new Ldap();
		EntityManagerFactory emf = null;
		EntityManager em = null;	
		
		LdapConnection conn = null;
		
		try {
			
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			test.createGroupAcl(em, conn);
			test.createGroupAcl(em, conn);
			log.info("...ACLs test has finished");
		}catch (Exception e) {
			log.error("Failed to test ACLs", e);
		}finally {
			db.closeEm(em);
			db.closeEmf(emf);
			ldap.closeLdapConnection(conn);
		}
		
		log.info("Program has finished... GOOD BYE!!!!");
	}
}
