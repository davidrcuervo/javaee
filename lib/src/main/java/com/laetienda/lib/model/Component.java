package com.laetienda.lib.model;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="components")
@NamedQueries({
		@NamedQuery(name="Component.findAll", query="SELECT c FROM Component c"),
		@NamedQuery(name="Component.findByName", query="SELECT c FROM Component c WHERE c.name = :name"),
		@NamedQuery(name="Component.findByJavaClassName", query="SELECT c FROM Component c WHERE c.javaClassName = :javaClassName")
		})
public class Component extends Objeto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"java_class_name\"", unique=true, nullable=false, length=254)
	private String javaClassName;

	@Column(name="\"description\"", unique=false, length=254)
	private String description;
	
	public Component() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}
