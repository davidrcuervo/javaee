package com.laetienda.install;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.logging.log4j.Logger;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;

import com.laetienda.dbentities.*;
import com.laetienda.myapptools.Aes;
import com.laetienda.myapptools.Settings;
import com.laetienda.mydatabase.Db;
import com.laetienda.myldap.Group;
import com.laetienda.myldap.Ldap;
import com.laetienda.myldap.User;

public class InstallData {
	static final Logger log = LogManager.getLogger(InstallData.class);
	
	Db db;
	Ldap ldap;
	User sysadmin, manager, tomcat;
	Group sysadmins, managers;
	AccessList aclSysadmin, aclManager, aclAll, aclOwner, aclGroup;
	
	public InstallData() {
		db = new Db();
		ldap = new Ldap();
	}
	
	public void run() {
		
		EntityManagerFactory emf = null;
		LdapConnection conn = null;
		EntityManager em = null;
		
		try {
			emf = db.createEntityManagerFactory();	
			em = emf.createEntityManager();
			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
			
			createObjects(em, conn);
			
			log.info("Database has installed succesfully");
		} catch (Exception ex) {
			log.error("Failed to insert data in app.", ex);
		} finally {
			
			db.closeEm(em);
			db.closeEmf(emf);
			ldap.closeLdapConnection(conn);
		}
	}
	
	public void createObjects(EntityManager em, LdapConnection conn) throws Exception {
		createUsers(conn);
		createGroups(conn);
		createAcls(em, conn);
		db.commit(em);
	}
	
	private void createUsers(LdapConnection conn) throws Exception {
		CreateUsers users = new CreateUsers();
		sysadmin = users.createSysadmin(conn);
		manager = users.createManager(conn);
		tomcat = users.createTomcat(conn);
	}
	
	private void createGroups(LdapConnection conn) throws Exception {
		CreateGroups groups = new CreateGroups();
		sysadmins = groups.createSysadmins(conn);
		managers = groups.createManagers(conn);
	}
	
	private void createAcls(EntityManager em, LdapConnection conn) throws Exception {

		CreateAcls acls = new CreateAcls(conn);
		aclSysadmin = acls.createSysadminAcl(em, conn);
		aclManager = acls.createManagersAcl(em, conn);
		aclAll = acls.createAllAcl(em, conn);
		aclOwner = acls.createOwnerAcl(em, conn);
		aclGroup = acls.createGroupAcl(em, conn);
		
		db.insert(aclSysadmin, em);
		db.insert(aclManager, em);
		db.insert(aclAll, em);
		db.insert(aclOwner, em);
		db.insert(aclGroup, em);
	}
	
	@Deprecated
	public void installData(LdapConnection conn, EntityManager em, String tomcatPass) throws Exception {
		log.info("Installing data into database and LDAP...");

	
		Variable languages = new Variable("languages", "Languages availables in the system");
		Option language = new Option("en", "English");
		languages.addOption(language);
		languages.addOption("none", "Select a language");
		languages.addOption("es", "Espanol");
		languages.addOption("fr", "Francais");
		
		Form form = new Form("group", "com.laetienda.entities.Group", "/WEB-INF/jsp/email/signup.jsp", "/WEB-INF/jsp/thankyou/signup.jsp", aclManager);
		form.addInput(new Input(form, "name", "Group Name", "string", "Insert the group name", "glyphicon-user", true));
		form.addInput(new Input(form, "description", "Description", "string", "Insert description of the group", "glyphicon-user", true));
	}
	
	public static void main(String[] args){
		
		log.info("Starting InstallData class...");
		
		InstallData installer = new InstallData();
		installer.run();
		
		log.info("... test has finished.... GOOD BYE!!!");
	}
}
