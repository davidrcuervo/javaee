package com.laetienda.backend.service;

import java.util.List;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Ldap;

import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.UserRepository;
import com.laetienda.lib.model.UserJson;

public class UserService implements UserRepository {
	private final static Logger log = LogManager.getLogger(UserService.class);
	
	private Ldap ldap;
	
	public UserService() {
		ldap = new Ldap();
	}

	@Override
	public List<User> findAll(String username, String password) {

		//TODO Implement method in ldap engine.
		
		List<User> result = null;
//		LdapConnection conn = null;
//		try {
//			conn = ldap.getLdapConnection(username, password);
//			ldap.findAllUsers(conn);
//		}catch (Exception e) {
//			log.warn("Failed to find all users from directory. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
//			log.debug("Failed to find all users from directory.", e);
//		}finally {
//			ldap.closeLdapConnection(conn);
//		}
		
		return result;
	}

	@Override
	public User findByUsername(String username, String password, String uid) {
		User result = null;
		
		LdapConnection conn = null;
		
		try {
			conn = ldap.getLdapConnection("uid=" + username + "," + Ajustes.LDAP_PEOPLE_DN, password);
			result = ldap.findUser(uid, conn);
		}catch (Exception e) {
			log.warn("Failed to find user from directory. $uid: {} - $exception: {} -> {}", uid, e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to find user from directory. $uid: {}.", uid, e);
		}finally {
			ldap.closeLdapConnection(conn);
		}
		
		return result;
	}

	@Override
	public User add(String username, String password, String newUsername, String name, String lastname, String email,
			String pass1, String pass2) {
		User result = null;
		LdapConnection conn = null;
		
		try {
			conn = ldap.getLdapConnection("uid=" + username + "," + Ajustes.LDAP_PEOPLE_DN, password);
			result = new User(newUsername, name, lastname, email, pass1, pass2, conn);
			ldap.insertLdapEntity(result, conn);

		}catch (Exception e) {
			if(result != null)result.addError("User", "Internal error while saving in ldap direcotry");
			log.warn("Failed to add user into directory. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to add user into directory.", e);
		}finally {
			ldap.closeLdapConnection(conn);
		}
		
		return result;
	}
	
	public User update(String username, String password, UserJson u) {
		User result = null;
		LdapConnection conn = null;
		
		try {
			conn = ldap.getLdapConnection("uid=" + username + "," + Ajustes.LDAP_PEOPLE_DN, password);
			result = ldap.findUser(u.getUid(), conn);
			
			if(u.getCn() != null) result.setCn(u.getCn());
			if(u.getSn() != null) result.setSn(u.getSn());
			if(u.getMail() != null) result.setEmail(u.getMail(), conn);
			if(u.getPass1() != null) result.setPassword(u.getPass1(), u.getPass2());
			
			ldap.modify(result, conn);
		}catch(Exception e) {
			if(result != null)result.addError("User", "Internal error while saving in ldap direcotry");
			log.warn("Failed to add user into directory. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to add user into directory.", e);
		}finally {
			ldap.closeLdapConnection(conn);
		}
		
		return result;
	}

	@Override
	public boolean delete(String username, String password, String uid) {
		boolean result = false;
		LdapConnection conn = null;
		User user = null;
		try {
			conn = ldap.getLdapConnection("uid=" + username + "," + Ajustes.LDAP_PEOPLE_DN, password);
			user = ldap.findUser(uid, conn);
			conn.delete(user.getLdapEntry().getDn());
			result = ldap.findUser(uid, conn) == null;

		}catch (Exception e) {
			if(user != null) user.addError("User", "Internal error deleting user from ldap direcotry");
			log.warn("Failed to delete user from directory $uid: {} - $exception: {} -> {}", uid, e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to delete user from directory $uid: {}", uid, e);
		}finally {
			ldap.closeLdapConnection(conn);
		}
		
		return result;
	}
}
