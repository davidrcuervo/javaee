package com.laetienda.myldap;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.engine.Ldap;

import com.laetienda.myapptools.FormBeanInterface;
import com.laetienda.myapptools.Mistake;
import com.laetienda.myapptools.MyAppTools;
import com.laetienda.myapptools.Settings;

public class Group implements Serializable, FormBeanInterface, LdapEntity {

	private static final Logger log = LogManager.getLogger(Group.class);
	private static final long serialVersionUID = 1L;
	private Entry ldapEntry;
	private MyAppTools tools;
	private Ldap ldap;
	private List<Modification> modifications = new ArrayList<Modification>();
	private HashMap<String, List<String>> errors = new HashMap<String, List<String>>();
	private List<Mistake> errores = new ArrayList<Mistake>();
	
	public Group() {
		tools = new MyAppTools();
		ldap = new Ldap();
	}
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @param owner
	 * @param conn
	 * @throws Exception
	 */
	public Group(String name, String description, User owner, LdapConnection conn) throws Exception {
		tools = new MyAppTools();
		ldap = new Ldap();
		setName(name, conn);
		setDescription(description);
		setOwner(owner, conn);
		addMember(owner, conn);
	}
	
	public Group(String groupName, LdapConnection conn) throws Exception {
		tools = new MyAppTools();
		ldap = new Ldap();
		
		try {
			Dn dn = new Dn("cn=" + groupName, "ou=groups", Settings.LDAP_DOMAIN);
			findGroup(dn, conn);
		} catch (LdapInvalidDnException | IOException e) {
			log.warn("Failed to create group object. $groupName: {} - $error: {}", groupName, e.getMessage());
			throw e;
		}
	}
	
	public Group(Dn groupDn, LdapConnection conn) throws LdapException, IOException {
		tools = new MyAppTools();
		ldap = new Ldap();
		findGroup(groupDn, conn);
	}
	
	private void findGroup(Dn group, LdapConnection conn) throws IOException, LdapException {
		
		try {
			Entry temp = conn.lookup(group);

			if(temp == null) {
				log.warn("Group does not exist");
				throw new IOException();
			}else {
				ldapEntry = temp;
			}
			
		} catch (LdapException e) {
			log.warn("Failed to find grup. $error: {}", e.getMessage());
			log.debug("Failed to find grup.", e);
			throw e;
		}
	}
	
	public Group setName(String groupName, LdapConnection conn) throws Exception {
		String temp = "cn=" + groupName + ",ou=groups," + Settings.LDAP_DOMAIN;
		log.debug("$GroupDn: {}", temp);
		setLdapEntry(temp, conn);
		return this;
	}
	
	public Group setLdapEntry(String dnstr, LdapConnection conn) throws Exception {
		log.info("Setting LDAP entry...");
		
		try {
			Dn dn = ldap.buildDn(dnstr);
			if(conn.exists(dn)) {
				log.warn("Failed to set Group LDAP Entry, it already exists");
				addError("cn", "A group with this name already exists");
				ldapEntry = conn.lookup(dn);
			}else {
				ldapEntry = new DefaultEntry(dn);
				ldapEntry
						.add("objectclass", "groupOfUniqueNames")
						.add("cn", dn.getRdn(0).getValue());
			}
			
			log.info("... LDAP entry set succesfully");
		} catch (Exception e) {
			log.warn("Failed to set LDAP Entry");
			throw e;
		}
		
		return this;
	}
	
	@Override
	public Entry getLdapEntry() {
		return ldapEntry;
	}
	
	public Group setOwner(User user, LdapConnection conn) {
		Dn dn = user.getLdapEntry().getDn();
		
		try {
			if(ldapEntry.get("owner") == null) {
				ldapEntry.add("owner", dn.getName());
			}else {
				Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "owner", dn.getName());
				modifications.add(modification);
			}
			
			if(dn.getRdn(0).getName() == null || dn.getRdn(0).getName().isBlank()) {
				addError("owner", "The owner can't be empty");
			}else if(dn.getRdn(0).getName().length() > 254) {
				addError("owner", "The owner can't have more than 254 charcters");
			}
			
		} catch (LdapException e) {
			log.warn("Failed to set owner of group.");
			log.debug("Failed to set owner of group", e);
			addError("owner", "Internal error does not allow to set owner");
		}
		
		
		return this;
	}
	
	public Group setDescription(String description) {

		try {
			if(ldapEntry.get("description") == null) {
				ldapEntry.add("description", description);
			}else {
				Modification modification = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "description", description);
				modifications.add(modification);
			}
			
			if(description == null || description.isEmpty()) {
				log.debug("description is empty");
			}else if(description.length() > 254) {
				addError("description", "The description of the group can't have more than 254 charcters");
			}
			
		} catch (LdapException e) {
			log.warn("Failed to set owner of group.");
			log.debug("Failed to set owner of group", e);
			addError("description", "Internal error does not allow to set description");
		}
		
		return this;		
	}
	
	public Group addMember(User user, LdapConnection conn) {
		
		addMember(user.getLdapEntry().getDn(), conn);
		
		return this;
	}
	
	private Group addMember(Dn member, LdapConnection conn) {
		String username = member.getRdn(0).getValue();
		log.debug("Adding member to group. $group: {} - $member: {}", ldapEntry.getDn().getName(), member.getName());		
		try {
			if(conn.exists(member)) {
				
				if(username == null || username.isBlank()) {
					addError("member", "Member, " + username + ", can not be empty");
				}
				
				if(username.length() > 255) {
					addError("member", "Member cant have more than 254 letters");
				}
				
				if(ldapEntry.contains("uniqueMember", member.getName())) {
					addError("member", new String("Member, " + username + ", is part of this group"));
				}else {
					if(conn.exists(ldapEntry.getDn())) {
						Modification modi = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, "uniqueMember", member.getName());
						modifications.add(modi);
					}else {
						ldapEntry.add("uniqueMember", member.getName());
						
					}
				}
				
			}else {
				addError("member", "Member, " + username + " ,does not exist");
			}
			
		} catch (LdapException e) {
			log.warn("Failed to add member to group", e.getMessage());
			log.debug("Failed add member to group", e);
			addError("member", "Member entry is not valid");
		}
		
		return this;
	}
	
	public boolean isMember(User user, LdapConnection conn) {
		log.debug("Checking if user exists in group. $user: {}", user.getLdapEntry().getDn().getName());
		return ldapEntry.contains("uniqueMember", user.getLdapEntry().getDn().getName()); 
	}
	
	 public List<User> getMembers(LdapConnection conn) throws LdapException {
		 log.info("Getting list of users from group...");
		 
		 List<User> result = new ArrayList<User>();
		 
		 try {
			 for(Value<?> val : ldapEntry.get("uniquemember")) {
				 
				User user = new User(conn.lookup(val.getString()));
				result.add(user);
			 
			 }
			 log.info("...List of users has found succesfully");
		 } catch (LdapException e) {
			 log.warn("Failed to find list of users from group. $error: {}", e.getMessage());
			 throw e;
		 }
		 
		 return result;
	 }
	 
	 @Deprecated
	 public Group removeMember(String username, LdapConnection conn) throws Exception { 
		 User user = new User(username, conn);
		 removeMember(user, conn);
		 return this;
	 }
	 
	 public Group removeMember(User member, LdapConnection conn) {
		 removeMember(member.getLdapEntry().getDn(), conn);
		 return this;
	 }
	 
	 private Group removeMember(Dn member, LdapConnection conn) {
		 String username = member.getRdn(0).getValue();
		 log.info("Removing user from group. $group: {} - $user: {}", getGroupName(), username);
		 
		 if(ldapEntry.contains("uniquemember", member.getName())) {
			 if(ldapEntry.get("owner").contains(member.getName())) {
				 addError("member" , "Member, " + username + ", is owner of the group and it can't be removed");
			 }else {
				 Modification modi = new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, "uniquemember", member.getName());
				 modifications.add(modi);
			 }
		 }else {
			 addError("member", "Member, " + username + ", does not exist in group, " + getGroupName());			 
		 }
		 
		 return this;
	 }
	 	 
	 public String getGroupName() {
		 return this.getLdapEntry().getDn().getRdn(0).getValue();
	 }
	 
	 private void addError(Mistake error) {
			if(errors == null) {
				errores = new ArrayList<Mistake>();
			}
			
			errores.add(error);
	}
	 
	@Override
	public void addError(String pointer, String detail) {
		errors = tools.addError(pointer, detail, this.errors);
		Mistake error = new Mistake(pointer, detail);
		addError(error);
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
	@Deprecated
	public HashMap<String, List<String>> getErrores() {
		return errors;
	}
	
	@Override
	public String getJsonErrors() {
		
		return tools.getJsonErrors(errores);
	}
	
	@Override
	public List<Modification> getModifications() {
		return modifications;
	}
	
	@Override
	public void clearModifications() {
		modifications = new ArrayList<Modification>();
	}

	@Override
	public void reloadLdapEntry(LdapConnection conn) throws LdapException {
		Dn temp = ldapEntry.getDn();
		
		if(conn.exists(ldapEntry.getDn())) {
			ldapEntry = conn.lookup(temp);
		}
	}
}
