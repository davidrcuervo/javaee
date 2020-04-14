package com.laetienda.dbentities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.Logger;

import com.laetienda.myldap.Group;
import com.laetienda.myldap.Ldap;
import com.laetienda.myldap.User;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;

@Entity
@Table(name="access_control_lists")
@NamedQueries({
	@NamedQuery(name="AccessList.findall", query="SELECT acl FROM AccessList acl"),
	@NamedQuery(name="AccessList.findByName", query="SELECT acl FROM AccessList acl WHERE acl.name = :name")
//	@NamedQuery(
//			name="AccessList.findUsers", 
//			query="SELECT u1 FROM AccessList acl "
//					+ "JOIN acl.users aclu "
//					+ "JOIN aclu.user u1 "
//					+ "WHERE acl = :acl "
//					+ "UNION "
//					+ "SELECT u2 FROM AccessList acl "
//					+ "JOIN acl.groups aclg "
//					+ "JOIN aclg.group g "
//					+ "JOIN g.users u2 "
//					+ "WHERE acl = :acl"
//				),
//	@NamedQuery(
//			name="AccessList.findUserInAcl", 
//			query="SELECT acl FROM AccessList acl "
//					+ "JOIN acl.groups aclg "
//					+ "JOIN aclg.group g "
//					+ "JOIN g.users u2 "
//					+ "JOIN acl.users aclu  "
//					+ "JOIN aclu.user u1 "
//					+ "WHERE acl = :acl AND (u1 = :user OR u2 = :user)"
//			),
//	@NamedQuery(
//			name="AccessList.findAclsByUser", 
//			query="SELECT acl FROM AccessList acl "
//					+ "JOIN acl.groups aclg "
//					+ "JOIN aclg.group g "
//					+ "JOIN g.users u2 "
//					+ "JOIN acl.users aclu "
//					+ "JOIN aclu.user u1 "
//					+ "WHERE u1 = :user OR u2 = :user"
//			)
})

public class AccessList extends Objeto implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(AccessList.class);
	
	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"description\"", unique=false, nullable=false, length=254)
	private String description;
	
	@ElementCollection
	@CollectionTable(name="acl_users")
	private List<String> users;
	
	@ElementCollection
	@CollectionTable(name="acl_groups")
	private List<String> groups;
	
	public AccessList() {
		users = new ArrayList<String>();
		groups = new ArrayList<String>();
	}
	
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
	public AccessList(
				String name, String description,
				User owner, Group group,
				AccessList delete, AccessList write, AccessList read,
				EntityManager em, LdapConnection conn) throws Exception 
	{
		super(owner, group, delete, write, read, conn);
		setName(name, em);
		setDescription(description);
		addUser(owner, conn);
		addGroup(group, conn);
		
	}
	
	public AccessList(
				String name, String description,
				User owner, Group group,
				EntityManager em, LdapConnection conn
			) {
		setDefaultObjeto(owner, group, conn);
		setName(name, em);
		setDescription(description);
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
		return name;
	}

	public AccessList setName(String name, EntityManager em) {
		log.info("Setting name for access list. $name: {}", name);
		log.debug("$length: {}", name.length());
		
		this.name = name;
		
		try {
			if(name == null || name.isEmpty()) {
				log.warn("Failed to set Access List Name, it can't be empty");
				addError("Access Control List", "Failed to set Access List Name, it can't be empty");
			}else if(name.length() > 254) {
				log.warn("Failed to set Access List Name, it can't have more than 254 characters.");
				addError("Access Control List", "Failed to set Access List Name, it can't have more than 254 characters.");
			}else {
			
				List<AccessList> result = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", name).getResultList();
								
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
		return description;
	}

	public AccessList setDescription(String description) {
		this.description = description;
		
		if(description == null || description.isEmpty()) {
			log.warn("Failed to set Access List description, it can't be empty.");
			addError("Access Control List", "Failed to set Access List description, it can't be empty.");
		}else if(name.length() > 254) {
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
		
		if(users == null) {
			log.debug("users variable has not been initialized");
			users = new ArrayList<String>();
		}
		
		try {
			if(conn.exists(user.getLdapEntry().getDn())) {
				if(users.contains(user.getUid())){
					log.debug("{} already exists in acl {}", user.getUid(), getName());
				}else {
					users.add(user.getUid());
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
		}else if(users.contains(user.getUid())) {
			users.remove(user.getUid());
		}else {
			log.warn("User, {}, can't be removed because does not exist in the acl.", user.getUid());
		}
	}
	
	public void addGroup(String strGroup, LdapConnection conn) throws Exception {
		Group group = new Group(strGroup, conn);
		addGroup(group, conn);
	}
	
//	public void addGroup(Group group, EntityManager em, LdapConnection conn) {
	public void addGroup(Group group, LdapConnection conn) {	
		if(groups == null) {
			log.debug("groups variable has not been initialized");
			groups = new ArrayList<String>();
		}
		
		
		try {
			if(conn.exists(group.getLdapEntry().getDn())) {
				if(groups.contains(group.getGroupName())) {
					log.debug("{} group already exists in acl {}", group.getGroupName(), getName());
				}else {
					groups.add(group.getGroupName());
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
		}else if(groups.contains(group.getGroupName())) {
			groups.remove(group.getGroupName());
		}else {
			log.warn("Group, {}, can't be removed because does not exist in the acl: {}", group.getGroupName(), getName());
		}
	}
	
	public boolean isAuthorized(User user, LdapConnection conn){
		boolean result = false;
		Ldap ldap = new Ldap();
		
		if(users.contains(user.getUid())) {
			result = true;
		}else {
			for(String temp : groups) {
				Group group = ldap.findGroup(temp, conn);
				if(group != null && group.isMember(user, conn)){
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		log.info("Hello " + AccessList.class.getName() + "!!!.");
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.laetienda.database");
		AccessList result = null;
		List<String> result2 = new ArrayList<String>();
		EntityManager em = emf.createEntityManager();
		try {

			AccessList acl = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", "managers").getSingleResult();
			String user = em.createNamedQuery("User.findByUid", String.class).setParameter("uid", "manager").getSingleResult();
			result = em.createNamedQuery("AccessList.findUserInAcl", AccessList.class).setParameter("user", user).setParameter("acl", acl).getSingleResult();
			result2 = em.createNamedQuery("AccessList.findUsers", String.class).setParameter("acl", acl).getResultList();
		}catch( NoResultException | NonUniqueResultException e) {
			log.info("No result found", e);
		}finally{
			em.close();
			emf.close();
		}
		
		log.info("RESULT1: " + (result != null ? result.getName() : "No access"));
		
		log.info("RESULT2: ");
		for(String username : result2) {
			log.info(username);
		}
		
		log.info("GAME OVER!!!");
	}
}
