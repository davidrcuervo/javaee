package com.laetienda.myldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laetienda.myapptools.Settings.*;

public class Ldap {
	private final static Logger log = LogManager.getLogger(Ldap.class);
	
	private List<LdapConnection> connections = new ArrayList<LdapConnection>();
	
	public synchronized LdapConnection getLdapConnection(String ldapUser, String password) throws Exception {
		log.info("Creating LdapConnection...");
		
		LdapConnection result = new LdapNetworkConnection(HOSTNAME, LDAP_PORT, true);
		result.bind(ldapUser, password);
		connections.add(result);
		log.info("... LdapConnection created succesfully");
		return result;
	}
	
	public void closeLdapConnection(LdapConnection connection) {
		log.info("Closing LdapConnection....");
		
		try {
			if(connection != null) {
				if(connection.isConnected() || connection.isAuthenticated()) {
						connection.unBind();
				}
				
				if(connection.isConnected()){
					connection.close();
				}
			}
		
			connections.remove(connection);
		} catch (LdapException | IOException e) {
			log.error("Failed to close connection. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to close connection", e);
		}
		
		log.info("...Ldap Connection closed succesfully");
	}
	
	public Dn buildDn(String dn) throws LdapInvalidDnException {
		log.info("Building Dn object. $dn: {}", dn);
		Dn result = null;
		
		try {
			result = new Dn(dn);
		} catch (LdapInvalidDnException e) {
			log.warn("Failed to create Dn.");
			throw e;
		}
		
		return result;
	}
	
	/**
	 * @param ldapEntity
	 * @param conn
	 * @throws LdapException
	 */
	public void insertLdapEntity(LdapEntity ldapEntity, LdapConnection conn) throws LdapException {
		log.info("Inserting LdapEntity into ldap...");
		
		try {
			log.debug("LdapEntity Dn. $dn: {}", ldapEntity.getLdapEntry().getDn().getName());
			
			if(log.isDebugEnabled()) {
				Iterator<Attribute> iterator = ldapEntity.getLdapEntry().getAttributes().iterator();
				while(iterator.hasNext()) {
					Attribute atr = iterator.next();
					log.debug("$Attribute: {} -> $Value: {}", atr.getId(), atr.get());
				}
			}
			
			if(ldapEntity.getErrors().size() > 0 ) {
				log.warn("LdapEntity was not added. User input not valid");
			}else {
				conn.add(ldapEntity.getLdapEntry());
				log.info("... LdapEntity has been inserted into ldap succesfully");
			}
		} catch (LdapException e) {
			log.error("Failed to insert LdapEntity into ldap. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			throw e;
		}
	}
	
	public User findUser(String username, LdapConnection conn) {
		log.info("Searching user in ldap. $useranme: {}", username);
		
		User result = null;
		try {
			result = new User(username, conn);
		} catch (Exception e) {
			log.warn("User does not exist in ldap. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("User does not exist in ldap.", e);
		}
		
		return result;
	}
	
	public Group findGroup(String group, LdapConnection conn) {
		log.info("Searching group in ldap. $groupname: {}", group);
		
		Group result = null;
		try {
			result = new Group(group, conn);
		} catch (Exception e) {
			log.warn("Group does not exist in ldap. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Group does no exist in ldap.", e);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param ldapEnity
	 * @param conn
	 * @throws LdapException
	 */
	public void modify(LdapEntity ldapEnity, LdapConnection conn) throws LdapException {
		log.info("Modifying LdapEntity in ldap...");
		try {
			
			if(ldapEnity.getErrors().size() > 0) {
				log.warn("Failed to modify LdapEntity due to invalid user input");
			}else {
				
				for(Modification modification : ldapEnity.getModifications()) {
					conn.modify(ldapEnity.getLdapEntry().getDn(), modification);
				}
				
				ldapEnity.clearModifications();
				ldapEnity.reloadLdapEntry(conn);
				log.info("... LdapEntity has been modifying succesfully");
			}
		} catch (LdapException e) {
			log.error("Failed to modify LdapEntity in ldap. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			throw e;
		}
	}
	
	public void ldapEntity(LdapEntity ldapEntity, LdapConnection conn) throws LdapException {
		log.info("removing LdapEntity from ldap...");
		
		try {
			conn.delete(ldapEntity.getLdapEntry().getDn());
			log.info("...LdapEntity removed form LDAP succesfully");
		} catch (LdapException e) {
			log.info("failed to remove user from LDAP");
			throw e;
		}
	}
	
	public Entry getPeopleLdapEntry(LdapConnection conn) throws Exception{
		log.info("Getting people ldap entry...");
		Entry result = null;
		
		try {
			Dn peopleDn = new Dn("ou=People", LDAP_DOMAIN);
			result=conn.lookup(peopleDn);
			log.info("... people Dn has been found. $peopleDn: {}", result.getDn().getName());
		} catch (Exception e) {
			log.warn("failed to get people ldap entry");
			throw e;
		}
		
		return result;
	}
}
