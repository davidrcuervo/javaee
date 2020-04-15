package org.laetienda.engine;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.model.AccessList;
import com.laetienda.model.Objeto;
import com.laetienda.myapptools.Settings;
import com.laetienda.myauth.AuthTables;
import com.laetienda.myldap.Group;
import com.laetienda.myldap.User;

public class Authorization {
	private final static Logger log = LogManager.getLogger(Authorization.class);
	
	private static int ACL_ALL_ID;

	private User user;
	private LdapConnection conn;
	private AuthTables tables;
	private Ldap ldap;
	private boolean installFlag;

	/**
	 * 
	 * @param username provide simple username (uid). Do not provide full Dn.
	 * @param password
	 * @param tables
	 */
	public Authorization(String username, String password, AuthTables tables) {
		
		this.tables = tables;
		ldap = new Ldap();
		user = null;
		conn =null;
		installFlag = false;
		
		try {
			Dn dn = ldap.buildDn("uid=" + username + ",ou=People," + Settings.LDAP_DOMAIN);
			LdapConnection temp = ldap.getLdapConnection(dn.getName(), password);
			
			if(temp.isConnected() && temp.isAuthenticated()) {
				conn = temp;
				user = ldap.findUser(username, conn);
			}
			
		} catch (Exception e) {
			log.warn("Failed to authenticate user. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to authenticate user.", e);
		}
	}
	
	public Authorization(LdapConnection conn) {
		installFlag = true;
		this.conn = conn;
	}
	
	public boolean canDelete(Objeto obj) throws LdapException {
		boolean result = false;
		
		if(isAuthenticated(obj)) {
			if(tables.isInWriteTable(obj.getId(), user.getUid())) {
				result = true;
				log.debug("{} is authorized in delete table for object.", user.getUid(), obj.getName());
			}else {
				if(isAuthorized(obj, obj.getDelete())) {
					result = true;
					tables.addInDeleteTable(obj.getId(), user.getUid());
				}else {
					obj.addError("Objeto", "It does not have enough rights to read");
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
					obj.addError("Objeto", "It does not have enough rights to write");
					log.warn("{} does have enough rights to write objeto: {}", user.getUid(), obj.getName());
				}
			}
		}
			
		return result;
	}
	
	public boolean canRead(Objeto obj) throws LdapException {
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
					obj.addError("Objeto", "It does not have enough rights to read");
					log.warn("{} does have enough rights to read objeto: {}", user.getUid(), obj.getName());
				}
			}
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
		Group sysadmins = ldap.findGroup("sysadmins", conn);
		
		if(isAuthenticated(obj)) {
			if(installFlag) {
				result = true;
				log.debug("User has been authorized because installation flag is up");
			}else if(sysadmins != null && sysadmins.isMember(user, conn)) {
				result = true;
				log.debug("User is authorized because belongs to sysadmin group. $username: {}", user.getUid());
			}else if(user != null && obj.getOwner().equals(user.getUid())) {
				result = true;
				log.debug("User is authorized because because he/she is the owner of the objeto. $username: {} -> $objectName: {}", user.getUid(), obj.getName());
			}else if(acl.getId() == ACL_ALL_ID) {
				result = true;
				log.debug("User is auhtorized becuse access list allows everybody");
			}else if(acl.isAuthorized(user, conn)) {
				result = true;
			}else {
				log.debug("User {} is not authorized by acl {}", user.getCn(), acl.getName());
			}
		}
		
		return result;
	}
	
	private boolean isAuthenticated(Objeto obj) {
		boolean result = false;
		
		if(conn.isAuthenticated()) {	
			result = true;
		}else {
			obj.addError("App", "User is not authenticated.");
			log.warn("User is not authenticated. $username: {}", user.getUid());
		}
		
		return result;
	}
	
	public User getUser() {
		return user;
	}

	public LdapConnection getConn() {
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
