package com.laetienda.myauth;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.dbentities.AccessList;
import com.laetienda.dbentities.Objeto;
import com.laetienda.myapptools.Settings;
import com.laetienda.myldap.Group;
import com.laetienda.myldap.Ldap;
import com.laetienda.myldap.User;

public class Authorization {
	private final static Logger log = LogManager.getLogger(Authorization.class);
	
	private User user;
	private LdapConnection conn;
	private AuthTables tables;
	private Ldap ldap;
	
	public Authorization(String username, String password) {
		Ldap ldap = new Ldap();
		
		ldap = new Ldap();
		user = null;
		conn =null;
		
		try {
			Dn dn = ldap.buildDn("cn=" + username + ",ou=People," + Settings.LDAP_DOMAIN);
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
	
	public boolean canRead(Objeto obj) throws LdapException {
		boolean result = false;
		
		if(conn.isAuthenticated()) {
			if(tables.isInReadTable(obj.getId(), user.getUid())) {
				result = true;
			}else {
				if(isSysadmin(obj)) {
					result = true;
					tables.addInReadTable(obj.getId(), user.getUid());
				}else {
					if(obj.getRead().isAuthorized(user, conn)) {
						tables.addInReadTable(obj.getId(), user);
						result =true;
					}
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
	private boolean isSysadmin(Objeto obj) throws LdapException {
		boolean result = false;
		Group sysadmins = ldap.findGroup("sysadmin", conn);
		
		if(isAuthenticated(obj)) {
			if(sysadmins.isMember(user, conn)) {
				result = true;
			}else if(obj.getOwner().equals(user.getUid())) {
				result = true;
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
	
	
}
