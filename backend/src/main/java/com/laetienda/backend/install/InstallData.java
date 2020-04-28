package com.laetienda.backend.install;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;
import org.laetienda.backend.engine.Ldap;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;

import com.laetienda.backend.model.*;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.backend.repository.ComponentRepository;
import com.laetienda.lib.model.AccessList;

public class InstallData {
	static final Logger log = LogManager.getLogger(InstallData.class);
	
	Db db;
	Ldap ldap;
	User sysadmin, manager, tomcat;
	Group sysadmins, managers;
	AccessListRepository aclSysadmin, aclManager, aclAll, aclOwner, aclGroup;
	
	public InstallData() {
		db = new Db();
		ldap = new Ldap();
	}
	
//	public void run() {
//		
//		EntityManagerFactory emf = null;
//		LdapConnection conn = null;
//		EntityManager em = null;
//		
//		try {
//			emf = db.createEntityManagerFactory();	
//			em = emf.createEntityManager();
//			String password = new Aes().decrypt(Settings.LDAP_ADIN_AES_PASSWORD, Settings.LDAP_ADMIN_USER);
//			conn = ldap.getLdapConnection(Settings.LDAP_ADMIN_USER, password);
//			createObjects(em, conn, auth);
//			
//			log.info("Database has installed succesfully");
//		} catch (Exception ex) {
//			log.error("Failed to insert data in app.", ex);
//		} finally {
//			
//			db.closeEm(em);
//			db.closeEmf(emf);
//			ldap.closeLdapConnection(conn);
//		}
//	}
	
	public void createLdapObjects(Authorization auth) throws Exception {
		LdapConnection conn = auth.getLdapConnection();
		createUsers(conn);
		createGroups(conn);
	}
	
//	public void createObjects(EntityManager em, Authorization auth) throws Exception {
	public void createDbObjects(EntityManager em, Authorization auth) throws Exception {
		LdapConnection conn = auth.getLdapConnection();
		createUsers(conn);
		createGroups(conn);
		
		createAcls(em, conn, auth);
		db.commit(em, auth);
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
	
	private void createAcls(EntityManager em, LdapConnection conn, Authorization auth) throws Exception {

		CreateAcls acls = new CreateAcls(conn);
		aclSysadmin = acls.createSysadminAcl(em, conn);
		aclManager = acls.createManagersAcl(em, conn);
		aclAll = acls.createAllAcl(em, conn);
		aclOwner = acls.createOwnerAcl(em, conn);
		aclGroup = acls.createGroupAcl(em, conn);
		ComponentRepository aclsComponent = new ComponentRepository("ACLs", "This component is mainly used to know who can create acls", AccessList.class, sysadmin, sysadmins, aclAll, aclAll, aclAll, em, conn );
		
		db.insert(aclsComponent.getObjeto(), em, auth);
		db.insert(aclSysadmin.getObjeto(), em, auth);
		db.insert(aclManager.getObjeto(), em, auth);
		db.insert(aclAll.getObjeto(), em, auth);
		db.insert(aclOwner.getObjeto(), em, auth);
		db.insert(aclGroup.getObjeto(), em, auth);
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
		
//		Form form = new Form("group", "com.laetienda.entities.Group", "/WEB-INF/jsp/email/signup.jsp", "/WEB-INF/jsp/thankyou/signup.jsp", aclManager);
//		form.addInput(new Input(form, "name", "Group Name", "string", "Insert the group name", "glyphicon-user", true));
//		form.addInput(new Input(form, "description", "Description", "string", "Insert description of the group", "glyphicon-user", true));
	}
}
