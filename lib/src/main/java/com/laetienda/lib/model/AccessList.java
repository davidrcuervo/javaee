package com.laetienda.lib.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="access_control_lists")
@NamedQueries({
	@NamedQuery(name="AccessList.findall", query="SELECT acl FROM AccessList acl"),
	@NamedQuery(name="AccessList.findByName", query="SELECT acl FROM AccessList acl WHERE acl.name = :name")
})

public class AccessList extends Objeto implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"description\"", unique=false, nullable=false, length=254)
	private String description;
	
	@ElementCollection
	@CollectionTable(name="acl_users")
	private List<String> users = new ArrayList<String>();;
	
	@ElementCollection
	@CollectionTable(name="acl_groups")
	private List<String> groups = new ArrayList<String>();
	
	public AccessList() {
		users = new ArrayList<String>();
		groups = new ArrayList<String>();
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

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
}
