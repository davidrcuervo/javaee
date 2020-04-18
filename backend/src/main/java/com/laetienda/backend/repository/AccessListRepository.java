package com.laetienda.backend.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Ldap;

import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.model.AccessList;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;

public class AccessListRepository extends ObjetoRepository implements RepositoryInterface{
	private static Logger log = LogManager.getLogger(AccessListRepository.class);
	
	private AccessList accessList;
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @param owner
	 * @param group
	 * @param delete
	 * @param write
	 * @param read
	 * @param em
	 * @param conn
	 * @throws Exception
	 */
	public AccessListRepository(
				String name, String description,
				User owner, Group group,
				AccessListRepository delete, AccessListRepository write, AccessListRepository read,
				EntityManager em, LdapConnection conn) throws Exception 
	{
		super(owner, group, delete, write, read, conn);
		accessList = new AccessList();
		setName(name, em);
		setDescription(description);
		addUser(owner, conn);
		addGroup(group, conn);
		
	}
	
	public AccessListRepository(
				String name, String description,
				User owner, Group group,
				EntityManager em, LdapConnection conn
			) {
		accessList = new AccessList();
		setDefaultObjeto(owner, group, conn);
		setName(name, em);
		setDescription(description);
	}
	
	public AccessListRepository(AccessList acl) {
		this.accessList = acl;
	}
	
	private void setDefaultObjeto(User owner, Group group, LdapConnection conn) {
		setOwner(owner, conn);
		setGroup(group, conn);
		super.setDelete(this);
		super.setWrite(this);
		super.setRead(this);
	}
	
	@Override
	public void setOwner(User owner, LdapConnection conn) {
		super.setOwner(owner, conn);
		addUser(owner, conn);
	}
	
	@Override
	public void setGroup(Group group, LdapConnection conn) {
		super.setGroup(group, conn);
		addGroup(group, conn);
	}
	
	public String getName() {
		return accessList.getName();
	}

	public AccessListRepository setName(String name, EntityManager em) {
		log.info("Setting name for access list. $name: {}", name);
		log.debug("$length: {}", name.length());
		
		accessList.setName(name);
		
		try {
			if(name == null || name.isEmpty()) {
				log.warn("Failed to set Access List Name, it can't be empty");
				addError("Access Control List", "Failed to set Access List Name, it can't be empty");
			}else if(name.length() > 254) {
				log.warn("Failed to set Access List Name, it can't have more than 254 characters.");
				addError("Access Control List", "Failed to set Access List Name, it can't have more than 254 characters.");
			}else {
			
				List<AccessListRepository> result = em.createNamedQuery("AccessList.findByName", AccessListRepository.class).setParameter("name", name).getResultList();
								
				if(result.size() > 0) {
					log.warn("Failed to set Access List, there is an Access List using the same name");
					addError("Access Control List", "There is an Access List using the same name");
				}
			}
		} catch(IllegalArgumentException e) {
			log.debug("Internal error. $exception: {}", e.getMessage());
			addError("Internal error", "$exception: " + e.getMessage());
		}
		
		return this;
	}

	public String getDescription() {
		return accessList.getDescription();
	}

	public AccessListRepository setDescription(String description) {
		accessList.setDescription(description);
		
		if(description == null || description.isEmpty()) {
			log.warn("Failed to set Access List description, it can't be empty.");
			addError("Access Control List", "Failed to set Access List description, it can't be empty.");
		}else if(description.length() > 254) {
			log.warn("Failed to set Access List description, it can't have more than 254 characters.");
			addError("Access Control List", "Failed to set Access List description, it can't have more than 254 characters.");
		}else {
			
		}
		
		return this;
	}

	public void addUser(String strUser, LdapConnection conn) throws Exception {
		User user = new User(strUser, conn);
		addUser(user, conn);
	}
	
	public void addUser(User user, LdapConnection conn) {
		
		if(accessList.getUsers() == null) {
			log.debug("users variable has not been initialized");
			accessList.setUsers(new ArrayList<String>());
		}
		
		try {
			if(conn.exists(user.getLdapEntry().getDn())) {
				if(accessList.getUsers().contains(user.getUid())){
					log.debug("{} already exists in acl {}", user.getUid(), getName());
				}else {
					accessList.getUsers().add(user.getUid());
					log.debug("{} has been added succesfully to acl {}", user.getUid(), getName());
				}
			}else {
				addError("user", "User does not exist in ldap directory");
			}
		} catch (LdapException e) {
			addError("user", "Failed to add user.");
			log.debug("Failed to insert user in access list", e);
		}
	}
	
	public void removeUser(User user) {
		if(user.getUid().equals(getOwner())){
			addError("User", "User can't be removed because it is the owner of the acl");
			log.warn("User, {}, can't be removed because it is the owner of the acl", user.getUid());
		}else if(accessList.getUsers().contains(user.getUid())) {
			accessList.getUsers().remove(user.getUid());
		}else {
			log.warn("User, {}, can't be removed because does not exist in the acl.", user.getUid());
		}
	}
	
	public void addGroup(String strGroup, LdapConnection conn) throws Exception {
		Group group = new Group(strGroup, conn);
		addGroup(group, conn);
	}
	
	public void addGroup(Group group, LdapConnection conn) {	
		if(accessList.getGroups() == null) {
			log.debug("groups variable has not been initialized");
			accessList.setGroups(new ArrayList<String>());
		}
		
		
		try {
			if(conn.exists(group.getLdapEntry().getDn())) {
				if(accessList.getGroups().contains(group.getGroupName())) {
					log.debug("{} group already exists in acl {}", group.getGroupName(), getName());
				}else {
					accessList.getGroups().add(group.getGroupName());
					log.debug("{} group has been added succesfully to acl: {}", group.getGroupName(), getName());
				}
			}else {
				addError("group", "User does not exist.");
			}
		} catch (LdapException e) {
			addError("group", "Failed to add user.");
			log.debug("Failed to insert group in access list", e);
		}
	}
	
	public void removeGroup(Group group) {
		if(group.getGroupName().equals(getGroup())){
			addError("Group", "Group can't be removed because it is the owner of the acl. $group: " + group.getGroupName());
			log.warn("User, {}, can't be removed because it is the owner of the acl: {}", group.getGroupName(), getName());
		}else if(accessList.getGroups().contains(group.getGroupName())) {
			accessList.getGroups().remove(group.getGroupName());
		}else {
			log.warn("Group, {}, can't be removed because does not exist in the acl: {}", group.getGroupName(), getName());
		}
	}
	
	public boolean isAuthorized(User user, LdapConnection conn){
		boolean result = false;
		Ldap ldap = new Ldap();
		
		if(accessList.getUsers().contains(user.getUid())) {
			result = true;
		}else {
			for(String temp : accessList.getGroups()) {
				Group group = ldap.findGroup(temp, conn);
				if(group != null && group.isMember(user, conn)){
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	public AccessList getAccessList() {
		return accessList;
	}
}
