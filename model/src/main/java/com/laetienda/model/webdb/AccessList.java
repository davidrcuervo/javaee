package com.laetienda.model.webdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="access_control_lists")
@NamedQueries({
	@NamedQuery(name="AccessList.findall", query="SELECT acl FROM AccessList acl"),
	@NamedQuery(name="AccessList.findByName", query="SELECT acl FROM AccessList acl WHERE acl.name = :name")
})

public class AccessList implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "objeto_id_seq", sequenceName = "objeto_id_seq", allocationSize=1)
	@GeneratedValue(generator = "objeto_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name="\"id\"")
	private Integer id;
	
	@Column(name="\"created\"", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar created;
	
	@Column(name="\"modified\"", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar modified;	
	
	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"description\"", unique=false, nullable=false, length=254)
	private String description;
	
	@ElementCollection
	@CollectionTable(name="acl_owners")
	private List<String> owners = new ArrayList<String>();
	
	@ElementCollection
	@CollectionTable(name="acl_users")
	private List<String> users = new ArrayList<String>();;
	
	@OneToMany
	private List<Group> groups = new ArrayList<Group>();

	public AccessList() {
		users = new ArrayList<String>();
		groups = new ArrayList<Group>();
	}
	
	@PrePersist
	public void onPrePersist() {
		setCreated(Calendar.getInstance()); 
	}
	
	@PreUpdate
	public void noPreUpdate() {
		setModified(Calendar.getInstance());
	}
	
	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public Calendar getModified() {
		return modified;
	}

	public void setModified(Calendar modified) {
		this.modified = modified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	public void addUser(String username) {
		if(users == null) {
			users = new ArrayList<String>();
		}
		
		users.add(username);
	}
	
	public void addGroup(Group group) {
		if(groups == null) {
			groups = new ArrayList<Group>();
		}
		
		groups.add(group);
	}
}
