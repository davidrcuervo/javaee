package com.laetienda.frontend.install;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.laetienda.frontend.engine.Settings;
import com.laetienda.lib.engine.HttpRequestException;
import com.laetienda.lib.engine.JsonDb;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Component;
import com.laetienda.lib.model.Form;
import com.laetienda.lib.model.UserJson;
import com.laetienda.lib.utilities.Aes;

public class InstallFrontend {
	private final static Logger log = LogManager.getLogger(InstallFrontend.class);
	
	private Settings settings;
	private JsonDb jdb;
	private Aes aes;
	private Gson gson;
	private String backendUrl;
	
	public InstallFrontend(String username, String password){
		settings = new Settings();
		aes = new Aes();
		gson = new Gson();

		backendUrl = settings.get("backend.url");
		
		jdb = new JsonDb(username, password);
		
	}
	
	private void doInstall() throws Exception {
		createBackenUser();
		createBackendAcl();
		createFormComponent();
	}
	
	private void createBackenUser() throws Exception {
		
		String username = settings.get("frontend.username");
		String password = aes.decrypt(settings.get("frontend.aes.password"), username);
		String url = backendUrl + settings.get("backend.user.path");
		
		UserJson user = new UserJson();
		user.setCn(username);
		user.setMail(settings.get("backend.mail"));
		user.setPass1(password);
		user.setPass2(password);
		user.setSn("Snless");
		user.setUid(username);
		
		try {
			jdb.post(url, gson.toJson(user));
		} catch (HttpRequestException e) {
			log.error("Failed to create user for frontend use. $code: {}, $error: {}", e.getResponse().getCode(), e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Failed to create user for frontend use. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage() );
			throw e;
		}
	}

	private void createBackendAcl() throws Exception{
		
		String aclurl = backendUrl + settings.get("backend.acl.path");
		
		try {
			AccessList all = gson.fromJson(jdb.get(aclurl + "/all"), AccessList.class);

			AccessList acl = new AccessList();
			acl.setOwner(settings.get("frontend.username"));
			acl.setGroup("sysadmins");
			acl.setName("frontend");
			acl.addUser("frontend");
			acl.addGroup("sysadmins");
			acl.setRead(all);
			acl.setDelete(acl);
			acl.setWrite(acl);
			
			jdb.post(aclurl, gson.toJson(acl));
			
		} catch (HttpRequestException e) {
			log.error("Failed to create frontend access list. $code: {} - $mensaje: {}", e.getResponse().getCode(), e.getMessage());
			throw e;
		} 
		
	}
	
	private void createFormComponent() {
		
		String dburl = backendUrl + settings.get("backend.db.path");
		String aclurl = backendUrl + settings.get("backend.acl.path");
		
		try {
			AccessList a = gson.fromJson(jdb.get(aclurl + "/all"), AccessList.class);
			AccessList f = gson.fromJson(jdb.get(aclurl + "/frontend"), AccessList.class);

			Component form = new Component();
			form.setName("forms");
			form.setDescription("Root component for frontend forms.");
			form.setOwner(settings.get("frontend.username"));
			form.setGroup("sysadmins");
			form.setDelete(f);
			form.setWrite(f);
			form.setRead(a);
			form.setJavaClassName(Form.class.getName());
			
			jdb.post(dburl, gson.toJson(form));

		} catch (HttpRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
	}



	public static void main(String[] args) {
		
		final String SYSADMIN = "sysadmin";
		final String SYSADMIN_AES_PASSWORD = "IkWlRQaCxCnS5xH7ZS1Vmex1UbunOp425j4k2gsNDudeSohHe5NqKpljZsXg2cTP19PQDA==";
		
		InstallFrontend install;
		try {
			String password = new Aes().decrypt(SYSADMIN_AES_PASSWORD, SYSADMIN);
			install = new InstallFrontend(SYSADMIN, password);
			install.doInstall();
		} catch (Exception e) {
			log.error("Failed to install required compoments for frontend use. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to install required compoments for frontend use.", e);
		}
	}

}
