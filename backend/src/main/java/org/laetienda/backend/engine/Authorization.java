package org.laetienda.backend.engine;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Objeto;

public class Authorization {
	private final static Logger log = LogManager.getLogger(Authorization.class);
	
	private static int ACL_ALL_ID;

	private User user;
	private LdapConnection conn;
	private AuthTables tables;
	private Ldap ldap;
	private boolean installFlag;

	/**
	 * It authenticate username and password. Test if auth.getUser is null to check if it get authorized.
	 * @param username provide simple username (uid). Do not provide full Dn.
	 * @param password
	 * @param tables
	 */
	public Authorization(String username, String password, AuthTables tables) {

		try {
			Dn dn = new Dn("uid=" + username + ",ou=People," + Ajustes.LDAP_DOMAIN);
			init(dn, password, tables);
		} catch (LdapInvalidDnException e) {
			log.error("Failed to create valid Dn from username. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
		}
	}
	
	public Authorization(Dn userDn, String password, AuthTables tables) {
		log.debug("running Authorization constructor by getting user Dn");
		init(userDn, password, tables);
	}
	
//	public Authorization(LdapConnection conn, AuthTables tables) {
////		installFlag = true;
//		this.tables = tables;
//		ldap = new Ldap();
//		this.conn = conn;
//	}
	
	private void init(Dn userDn, String password, AuthTables tables) {
		log.info("Authenticating user. $user: {}", userDn.getName());
		
		this.tables = tables;
		ldap = new Ldap();
		user = null;
		conn = null;
		installFlag = false;
		
		try {
			
			LdapConnection temp = ldap.getLdapConnection(userDn.getName(), password);
			
			if(temp.isConnected() && temp.isAuthenticated()) {
				conn = temp;
				user = ldap.findUser(userDn, conn);
				log.debug("User has authenticated succesfully. $user: {}", user.getLdapEntry().getDn().getName());
			}else {
				log.warn("Authentication failed. $userDn: {}", userDn.getName());
			}
			
		} catch (Exception e) {
			log.warn("Failed to authenticate user. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to authenticate user.", e);
		}
	}
	
	public boolean canDelete(Objeto obj) throws LdapException {
		boolean result = false;
		
		if(isAuthenticated(obj)) {
			if(isSysadmin() || tables.isInWriteTable(obj.getId(), user.getUid())) {
				result = true;
				log.debug("{} is authorized in delete table for object.", user.getUid(), obj.getName());
			}else {
				if(isAuthorized(obj, obj.getDelete())) {
					result = true;
					tables.addInDeleteTable(obj.getId(), user.getUid());
				}else {
//					obj.addError("Objeto", "It does not have enough rights to read");
					log.warn("{} does have enough rights to delete objeto: {}", user.getUid(), obj.getName());
				}
			}
		}
		
		return result;
	}
	
	public boolean canWrite(Objeto obj) throws LdapException {
		boolean result = false;
		
		if(isAuthenticated(obj)) {
			if(user != null && tables.isInReadTable(obj.getId(), user.getUid())) {
				result = true;
				log.debug("{} is authorized in write table for object.", user.getUid(), obj.getName());
			}else {
				if(isAuthorized(obj, obj.getWrite())) {
					result = true;
					tables.addInWriteTable(obj.getId(), user.getUid());
				}else {
//					obj.addError("Objeto", "It does not have enough rights to write");
					log.warn("{} does have enough rights to write objeto: {}", user.getUid(), obj.getName());
				}
			}
		}
			
		return result;
	}
	
	public boolean canRead(Objeto obj) throws LdapException, NullPointerException {
		boolean result = false;
		
		if(conn.isAuthenticated()) {
			if(tables.isInReadTable(obj.getId(), user.getUid())) {
				result = true;
				log.debug("{} is authorized in read table for object.", user.getUid(), obj.getName());
			}else {
				if(isAuthorized(obj, obj.getRead())) {
					result = true;
					tables.addInReadTable(obj.getId(), user.getUid());
				}else {
//					obj.addError("Objeto", "It does not have enough rights to read");
					log.warn("{} does have enough rights to read objeto: {}", user.getUid(), obj.getName());
				}
			}
		}

		return result;
	}
	
	public boolean isSysadmin() {
		boolean result = false;
		Group sysadmins = ldap.findGroup("sysadmins", conn);
		
		if(sysadmins != null && sysadmins.isMember(user, conn)) {
			result = true;
			log.debug("User is authorized because belongs to sysadmin group. $username: {}", user.getUid());
		}
		
		return result;
	}

	/**
	 * Check if it is sysadmin.
	 * @param obj
	 * @return
	 * @throws LdapException
	 */
	private boolean isAuthorized(Objeto obj, AccessList acl) throws LdapException {
		boolean result = false;
		
		
		if(isAuthenticated(obj)) {
			if(isSysadmin()) {
				result = true;
			}else if(user != null && obj.getOwner().equals(user.getUid())) {
				result = true;
				log.debug("User is authorized because because he/she is the owner of the objeto. $username: {} -> $objectName: {}", user.getUid(), obj.getName());
			}else if(acl.getId() == ACL_ALL_ID) {
				result = true;
				log.debug("User is auhtorized becuse access list allows everybody");
			}else if(isInAcl(acl)) {
				result = true;
			}else {
				log.debug("User {} is not authorized by acl {}", user.getCn(), acl.getName());
			}
		}
		
		return result;
	}
	
	private boolean isInAcl(AccessList acl) {
		boolean result = false;
		Ldap ldap = new Ldap();
		
		if(acl.getUsers().contains(user.getUid())) {
			result = true;
			log.debug("User is authorized becuase he/she exists in the users of the acl. $user: {} - $acl: {}", user.getUid(), acl.getName());
		}else {
			for(String temp : acl.getGroups()) {
				Group group = ldap.findGroup(temp, conn);
				if(group != null && group.isMember(user, conn)){
					result = true;
					log.debug("User is authorized becuase he/she exists in a group of the acl. $user: {} - $acl: {} - $group: {}", user.getUid(), acl.getName(), group.getName());
					break;
				}
			}
		}
		
		return result;
	}
	
	private boolean isAuthenticated(Objeto ojb) {
		boolean result = false;
		
		if(conn.isAuthenticated()) {	
			result = true;
		}else {
//			ojb.addError("App", "User is not authenticated.");
			log.warn("User is not authenticated. $username: {}", user.getUid());
		}
		
		return result;
	}
	
	protected boolean isInstallFlag() {
		return installFlag;
	}

	
	public User getUser() {
		return user;
	}

	public LdapConnection getLdapConnection() {
		return conn;
	}
	
	public void setConn(LdapConnection conn) {
		this.conn = conn;
	}
	
	public static void setACL_ALL_ID(int aCL_ALL_ID) {
		log.debug("Acl id that allows everybody is: {}", aCL_ALL_ID);
		ACL_ALL_ID = aCL_ALL_ID;
	}
}
