package com.laetienda.backend.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;

import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Objeto;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;

public class AccessListRepository extends ObjetoRepository implements RepositoryInterface{
	private static Logger log = LogManager.getLogger(AccessListRepository.class);
	
	private AccessList accessList;
	private Db db;
	
	/**
	 * 
	 * This constructor requires all parameters, included acl for delete, write and read
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
		accessList = new AccessList();
		createObjeto(accessList, owner, group, delete, write, read, conn);
		setName(name, em);
		setDescription(description);
		addUser(owner, conn);
		addGroup(group, conn);
		
	}
	/**
	 * This constructor does not requires acl for delete, write and read. Instead it will use the own acl.
	 * @param name
	 * @param description
	 * @param owner
	 * @param group
	 * @param em
	 * @param conn
	 * @throws Exception 
	 */
	public AccessListRepository(
				String name, String description,
				User owner, Group group,
				EntityManager em, LdapConnection conn
			) throws Exception {
		accessList = new AccessList();
		createObjeto(accessList, owner, group, this, this, this, conn);
//		setDefaultObjeto(owner, group, conn);
		setName(name, em);
		setDescription(description);
	}
	
	public AccessListRepository(AccessList acl) throws IOException {
		super(acl);
		if(acl == null) {
			log.warn("Input error. AccessList object can't be null.");
			throw new IOException("Input error. AccessList object can't be null.");
		}else {			
			this.accessList = acl;
		}
	}
	
	public AccessListRepository(String name, EntityManager em, Authorization auth) throws IOException {
		db = new Db();
		TypedQuery<?> query = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", name);
		AccessList acl = (AccessList)db.find(query, em, auth);
		
		if(acl == null) {
			throw new IOException("Failed to find objeto, requested name does not exist or user does not have rihgts to access Access List. $aclName: " + name) ;
		}else {
			setObjeto(acl);
		}
		
	}
	
	private void setDefaultObjeto(User owner, Group group, LdapConnection conn) {
//		super.setObjeto(this);
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
			removeUser(user.getUid());
		}else {
			log.warn("User, {}, can't be removed because does not exist in the acl.", user.getUid());
		}
	}
	
	private void removeUser(String user) {
		accessList.getUsers().remove(user);
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
			removeGroup(group.getGroupName());
		}else {
			log.warn("Group, {}, can't be removed because does not exist in the acl: {}", group.getGroupName(), getName());
		}
	}
	
	private void removeGroup(String group) {
		accessList.getGroups().remove(group);
	}

	public void merge(AccessList aclJson, EntityManager em, Authorization auth) throws Exception {
		
		super.merge(aclJson, em, auth);
		
		if(!getDescription().equals(aclJson.getDescription())) {
			setDescription(aclJson.getDescription());
		}
		
		for(String user : aclJson.getUsers()) {
			if(!getObjeto().getUsers().contains(user)) {	
				addUser(user, auth.getLdapConnection());
			}
		}
		
		Iterator<String> usersIterator = getObjeto().getUsers().iterator();
		while(usersIterator.hasNext()) {
			String user = usersIterator.next();
			if(!aclJson.getUsers().contains(user)) {
				usersIterator.remove();
			}
		}
		
		for(String group : aclJson.getGroups()) {
			if(!getObjeto().getGroups().contains(group)) {
				addGroup(group, auth.getLdapConnection());
			}
		}
		
		Iterator<String> groupIterator = getObjeto().getGroups().iterator();
		while(groupIterator.hasNext()) {
			String group = groupIterator.next();
			if(!aclJson.getGroups().contains(group)) {
				groupIterator.remove();
			}
		}
	}
	
	public AccessList getAccessList() {
		return accessList;
	}

	@Override
	public void setObjeto(Objeto objeto) {
		accessList = (AccessList)objeto;
		super.setParentObjeto(this.accessList);
	}
	
	public AccessList getObjeto() {
		return accessList;
	}
}
