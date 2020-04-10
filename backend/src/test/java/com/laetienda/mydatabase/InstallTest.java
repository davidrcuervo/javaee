package com.laetienda.mydatabase;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.laetienda.dbentities.AccessList;
import com.laetienda.install.InstallData;
import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;
import com.laetienda.myldap.Group;
import com.laetienda.myldap.Ldap;
import com.laetienda.myldap.User;

class InstallTest {
	private final static Logger log = LogManager.getLogger(InstallTest.class);

	InstallData installer = new InstallData();
	Ldap ldap;
	Db db;
	User sysadmin, manager, tomcat;
	Group sysadmins, managers;
	AccessList aclSysadmin, aclManager, aclAll, aclOwner, aclGroup;
	
	
	InstallTest(){
		ldap = new Ldap();
		db = new Db();
	}
	
	@Test
	void install() {
		
		EntityManagerFactory emf = null;
		EntityManager em =null;
		LdapConnection conn= null;
		
		try {
			emf = db.createEntityManagerFactory();
			em = emf.createEntityManager();
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			
			installer.createObjects(em, conn);
			testAcls(em, conn);
			
			
		}catch(Exception e) {
			fail("Critical exception caught. $exception: " + e.getClass().getSimpleName() + "-> " + e.getMessage());
			log.error("Test failed.", e);
		}finally {
			db.closeEm(em);
			db.closeEmf(emf);
			ldap.closeLdapConnection(conn);
		}
	}

	private void testAcls(EntityManager em, LdapConnection conn) throws Exception {
		
		int amountOfAcls = em.createNamedQuery("AccessList.findall", AccessList.class).getResultList().size();
		assertEquals(6, amountOfAcls, "number of Access List. Check if all acls are created correctly");
	}


}
