package com.laetienda.model.webdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.laetienda.lib.form.FormMethod;
import com.laetienda.lib.form.HtmlForm;
import com.laetienda.lib.form.InputForm;
import com.laetienda.lib.form.InputType;
import com.laetienda.model.lib.ValidateParameters;

/**
 * @author david
 *
 */
@Entity
@Table(name="grupo")
@NamedQueries({
	@NamedQuery(name="Group.findall", query="SELECT g FROM Group g"),
	@NamedQuery(name="Group.findByName", query="SELECT g FROM Group g WHERE g.name = :name")
})
@HtmlForm(name="Grupo")
public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "group_id_seq", sequenceName = "group_id_seq", allocationSize=1)
	@GeneratedValue(generator = "group_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name="\"id\"")
	private Integer id;
	
	@Column(name="\"created\"", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar created;
	
	@Column(name="\"modified\"", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar modified;
	
	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	@ValidateParameters(name="\"name\"", nullable=false, minlenght = 3, regex = "[a-zA-Z]+")
	@InputForm(name = "name", type = InputType.TEXT, label="Name", id="groupNameInput", placeholder="Insert name of group")
	private String name;	
	
	@Column(name="\"description\"", nullable = true, unique=false, length=254)
	@ValidateParameters(name = "\"description\"", maxlenght = 254)
	@InputForm(name="description", type = InputType.TEXT, label="Description", id="groupDescriptionInput", placeholder="Insert description of group (optional)")
	private String description;
	
	@ElementCollection
	@CollectionTable(name="group_owners")
	@ValidateParameters(name="owners", nullable=false)
	private List<String> owners;
	
	@ElementCollection
	@CollectionTable(name="group_members")
	@ValidateParameters(name="members", nullable=false)
	private List<String> members;
	
	public Group() {
		members = new ArrayList<String>();
		owners = new ArrayList<String>();
	}

	public String getName() {
		return this.name;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<String> getOwners() {
		return owners;
	}

	public void setOwners(List<String> owners) {
		this.owners = owners;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
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
}
