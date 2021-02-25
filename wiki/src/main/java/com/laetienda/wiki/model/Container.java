package com.laetienda.wiki.model;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Container
 *
 */
@Entity
public class Container implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "container_id_seq", sequenceName = "container_id_seq", allocationSize=1)
	@GeneratedValue(generator = "container_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name="\"id\"")
	private Integer id;
	
	@Column(name="\"created\"", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar created;
	
	@Column(name="\"modified\"", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar modified;
	
	@Column(name = "\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name = "\"path\"", unique=true, nullable=false, length=254)
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Container() {
		super();
	}
   
	@PrePersist
	public void onPrePersist() {
		this.created = Calendar.getInstance(); 
	}
	
	@PreUpdate
	public void noPreUpdate() {
		this.modified = Calendar.getInstance();
	}
	
}
