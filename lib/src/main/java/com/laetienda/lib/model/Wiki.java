package com.laetienda.lib.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name="wiki.findAll" , query="SELECT w FROM Wiki w"),
	@NamedQuery(name="wiki.findByName", query="SELECT w FROM Wiki w WHERE w.name = :name")
})
public class Wiki extends Objeto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"description\"", unique=false, length=254)
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {	
		return this.name;
	}
}
