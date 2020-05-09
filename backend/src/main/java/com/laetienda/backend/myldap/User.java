package com.laetienda.backend.myldap;

import static com.laetienda.backend.myapptools.Ajustes.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Ldap;

import com.laetienda.backend.myapptools.FormBeanInterface;
import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.lib.utilities.Mistake;
import com.laetienda.lib.utilities.Tools;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;


/**
 * @author myself
 *
 */

public class User implements Serializable, FormBeanInterface, LdapEntity{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(User.class);

	private String uid; 
	private Entry ldapEntry;
	private List<Modification> modifications = new ArrayList<Modification>();
	private Ldap ldap;
	private HashMap<String, List<String>> errors = new HashMap<String, List<String>>();
	private Tools tools;
	private List<Mistake> errores = new ArrayList<Mistake>();
	
	public User(Dn userDn, LdapConnection conn) throws Exception {
		ldap = new Ldap();
		tools = new Tools();
		
		ldapEntry = conn.lookup(userDn);
		
		if(ldapEntry == null) {
			log.warn("User does not exist in ldap. $username: {}", userDn.getName());
			throw new IOException("User does not exist. $username: " + userDn.getName());
		}
		
		this.uid = userDn.getRdn(0).getValue();
	}
	
	public User(String username, LdapConnection conn) throws Exception {
		ldap = new Ldap();
		tools = new Tools();
		
		Dn dn = new Dn("uid=" + username, "ou=people", LDAP_DOMAIN);
		ldapEntry = conn.lookup(dn);
		
		if(ldapEntry == null) {
			log.warn("User does not exist in ldap. $username: {}", username);
			throw new IOException("User does not exist. $username: " + username);
		}
		
		this.uid = username;
	}
	
	protected User(Entry entry) throws LdapInvalidAttributeValueException {
		ldap = new Ldap();
		tools = new Tools();
		
		try {
			this.uid = entry.get("uid").getString();
			this.ldapEntry = entry;
		} catch (LdapInvalidAttributeValueException e) {
			log.error("Invalid attribute (harcoded). $attribute: uid & $error: {}", e.getMessage());
			throw e;
		}
	}
	
/**
 * 
 * @param username
 * @param name
 * @param lastname
 * @param email
 * @param pass1
 * @param pass2
 * @param conn
 * @throws Exception
 */
//	public User(String username, String name, String lastname, String email, LdapConnection conn, EntityManager em) throws Exception {
	public User(String username, String name, String lastname, String email, String pass1, String pass2, LdapConnection conn) {	
		ldap = new Ldap();
		tools = new Tools();
		
		setUid(username, conn);
		setCn(name);
		setSn(lastname);
		setEmail(email, conn);
		setPassword(pass1, pass2);
	}

	public User setLdapEntry(LdapConnection conn) throws Exception {
		log.info("Setting user LDAP entry ...");		
		try {
			Dn dn = new Dn("uid=" + uid, Ajustes.LDAP_PEOPLE_DN);
			ldapEntry = conn.lookup(dn);
			
			if(ldapEntry == null) {				
				ldapEntry = new DefaultEntry(dn);
				ldapEntry.add("objectclass", "person")
					.add("objectclass", "inetOrgPerson")
					.add("uid", dn.getRdn(0).getValue())
					.add("ou", "People");
			}
			log.info("... user LDAP entry has been set succesfully");
		} catch (Exception e) {
			log.warn("Failed to set user LDAP entry");
			throw e;
		} 
		
		return this;
	}

	public String getUid() {
		return uid;
	}
	
	private void setUid(String username, LdapConnection conn){
		log.info("Seting username (uid)...");
		this.uid = username;
		
		try {
			if(uid == null || uid.isEmpty()) {
				addError("uid", "Username can't be empty");
			}else {
				if(uid.length() < 4) {
					addError("uid", "Username must have at least 4 characters");
				}
				
				if(uid.length() > 64) {
					addError("uid", "Username can't have more than 64 characters");
				}
				
				if(conn.lookup("uid=" + username + ", ou=people, " + Ajustes.LDAP_DOMAIN) != null) {
					addError("uid", "Username already exists");
				}else {
								
				}
				setLdapEntry(conn);
				log.info("... username (uid) has been set succesfully");
			}
		}catch(Exception e) {
			addError("uid", "Internal error");
			log.warn("Failed to set username (uid). $error: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to set username. $uid: {}", username, e);
		}
	}
	
	public String getEmail() throws LdapInvalidAttributeValueException {
		try {
			return ldapEntry.get("mail").getString();
		} catch (LdapInvalidAttributeValueException e) {
			log.warn("Failed to get email. $error: {}", e.getMessage());
			throw e;
		}
	}
	
	public void setEmail(String email, LdapConnection conn) {
		
		try {
			
			if(ldapEntry.get("mail") == null) {
				ldapEntry.add("mail", email);
			}else {
				Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "mail", email);
				modifications.add(modification);
			}
			
			if(email == null || email.isEmpty()) {
				addError("email", "The email can't be empty");
			}else if(email.length() > 254) {
				addError("email", "The mail can't have more than 255 charcters");
			}else {	
//				EntryCursor search = conn.search(ldap.getPeopleLdapEntry(conn).getDn(), "(mail=" + email + ")", SearchScope.ONELEVEL);
				EntryCursor search = conn.search(ldap.buildDn(LDAP_PEOPLE_DN), "(mail=" + email + ")", SearchScope.ONELEVEL);
			
				if(search.iterator().hasNext()) {
					addError("email", "This email address has already been registered");
				}
			}			
		} catch (Exception e) {
			addError("email", "The application were not able to find out if email has been registered");
			log.warn("Failed to se email. $email: {} - $exception: {} -> {}", email, e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to se email. $email: {}", email);
		}
	}

	/**
	 * 
	 * @param cn CN DAP Direcotory entry, common used for the First Name
	 * @throws DapException 
	 */
	public void setCn(String cn) {
		
		try {
			if(ldapEntry.get("cn") == null) {
				ldapEntry.add("cn", cn);
			}else {
				Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "cn", cn);
				modifications.add(modification);
			}
	
			if(cn == null || cn.isEmpty()) {
				addError("cn", "First Name can't be empty");
			}else {
				if(cn.length() > 254) {
					addError("cn", "The name can't have more than 255 charcters");
				}
				
				//TODO validate that cn has only letters, no numbers or special characters
			}
		} catch (LdapException e) {
			addError("cn", "The name is not valid");
		}
	}

	public String getCn() throws LdapInvalidAttributeValueException {
		String result = null;
		try {
			result = ldapEntry.get("cn").getString();
		} catch (LdapInvalidAttributeValueException e) {
			log.warn("Failed to find cn attribute. $error: {}", e.getMessage());
			throw e;
		}
		return result;
	}
	
	/**
	 * 
	 * @param sn SN (SureName) LDAP entry, common used for last name
	 * @throws LdapException 
	 */
	public void setSn(String sn) {
		
		try {
			if(ldapEntry.get("sn") == null) {
				ldapEntry.add("sn", sn);
			}else {
				Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "sn", sn);
				modifications.add(modification);
			}
			
			if(sn == null || sn.isEmpty()) {
				addError("sn", "Last name can't be empty");
			}else {
				if(sn.length() > 254) {
					addError("sn", "The last name can't have more than 255 charcters");
				}
				
				//TODO validate that sn has only letters, no numbers or special characters
			}
		} catch (LdapException e) {
			log.warn("Failed to set sn under ldap entry. $error: {}", e.getMessage());
			addError("sn", "The last name is not valid");
		}
	}	
	
	public String getSn() {
		String result = new String();
		String sn;
		
		try {
			sn = ldapEntry.get("sn").getString();
		
			if(sn == null || sn.equals("Snless")) {
				log.debug("sn is null or Snless. $sn: {}", sn);
			}else {
				result = sn;
			}
		
		} catch (LdapInvalidAttributeValueException e) {
			log.warn("Failed to get sn ldap entry. $error: {}", e.getMessage());
		}
		return result;
	}

	public void setPassword(String password, String password2) {
		
		try {
			if(ldapEntry.get("userPassword") == null) {
				ldapEntry.add("userPassword", password);
			}else {
				Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "userPassword", password);
				modifications.add(modification);
			}

			if(password == null || password.isEmpty()) {
				addError("password", "The password can't be empty");
			}else {
				if(!(password.equals(password2))) {
					addError("password", "The password and confirmation should be identical");
				}
				
				if(password.length() < 8) {
					addError("password", "The password must have at least 8 characters");
				}
				
				if(password.length() > 255) {
					addError("password", "The password can't have more than 255 characters");
				}
			}
		} catch (LdapException e) {
			log.warn("Exception adding password to ldap entry. $error: {}", e.getMessage());
			addError("password", "Password is not valid");
		}
	}	
	
	public void setDescription(String description) {
		
		try {
			if(ldapEntry.get("description") == null) {
				ldapEntry.add("description", description);
			}else {
				Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "description", description);
				modifications.add(modification);
			}
			
			if(description == null || description.isEmpty()) {
				addError("description", "Description can't be empty");
			}else {
				if(description.length() > 254) {
					addError("description", "The description can't have more than 255 charcters");
				}
			}
		} catch (LdapException e) {
			addError("description", "This not a valid description");
			log.warn("Failed to add description to ldap entry. $error: {}", e.getMessage());
		}
	}	
	
	public String getName() {
		String result = null;
		
		try {
			result = ldapEntry.get("cn").getString() + " " + ldapEntry.get("sn").getString();
		} catch (LdapInvalidAttributeValueException e) {
			result = new String();
			log.warn("EXCEPTION: while getting name from person ldap attribute. $error: {}", e.getMessage());
		}
		
		return result;
	}
	
	public String getDescription() {
		String result = new String();
		try {
			result = ldapEntry.get("description").getString();
		} catch (LdapInvalidAttributeValueException e) {
			result = new String();
			log.warn("EXCEPTION: while getting description from person ldap attribute. $error: ", e.getMessage());
		}
		return result;
	}
	
	@Override
	public void addError(String pointer, String detail) {
		errors = tools.addError(pointer, detail, this.errors);
		Mistake error = new Mistake(pointer, detail);
		addError(error);
	}
	
	@Override
	public HashMap<String, List<String>> getErrores(){
		return errors;
	}
	
	public Entry getLdapEntry() {
		return ldapEntry;
	}
	
	@Override
	public List<Modification> getModifications(){
		return modifications;
	}
	
	@Override
	public void clearModifications() {
		modifications = new ArrayList<Modification>();
	}
	
	private void addError(Mistake error) {
		if(errors == null) {
			errores = new ArrayList<Mistake>();
		}
		
		errores.add(error);
	}

	public String getJson() {
		
		String result = "{";
		result += "\"uid\": \"" + getUid() + "\",";
		result += "\"cn\": \"" + getLdapEntry().get("cn").get() + "\",";
		result += "\"sn\": \"" + getLdapEntry().get("sn").get() + "\",";
		result += "\"mail\": \"" + getLdapEntry().get("mail").get() + "\"";
		
		if(getErrores().size() > 0) {
			result += getJsonErrors();
		}
		result += "}";
		
		return result;
	}

	@Override
	public void reloadLdapEntry(LdapConnection conn) throws LdapException {
		ldapEntry = conn.lookup(getLdapEntry().getDn());
	}

	@Override
	public void addError(int status, String pointer, String title, String detail) {
		Mistake error = new Mistake(status, pointer, title, detail);
		addError(error);
		
	}

	@Override
	public void addError(int status, String pointer, String detail) {
		Mistake error = new Mistake(status, pointer, detail);
		addError(error);
		
	}

	@Override
	public List<Mistake> getErrors() { 
		return errores;
	}

	@Override
	public String getJsonErrors() {
		
		return tools.getJsonErrors(errores);
	}
}
