package com.laetienda.lib.model;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.*;

@Entity
@Table(name="objetos")
@NamedQueries({
	@NamedQuery(name="Objeto.findall", query="SELECT o FROM Objeto o")
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Objeto implements Serializable{	
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

	@Column(name="\"user\"", nullable=true, unique=false)
	private String owner;
	
	@Column(name="\"group\"", nullable=true, unique=false)
	private String group;
	
	@OneToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="\"read_acl_id\"", nullable=true, unique=false)
	private AccessList write;
	
	@OneToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="\"write_acl_id\"", nullable=true, unique=false)
	private AccessList read;
	
	@OneToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="\"delete_acl_id\"", nullable=true, unique=false)
	private AccessList delete;
	
	public Objeto() {}

	@PrePersist
	public void onPrePersist() {
		setCreated(Calendar.getInstance()); 
	}
	
	@PreUpdate
	public void noPreUpdate() {
		setModified(Calendar.getInstance());
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public AccessList getWrite() {
		return write;
	}

	public void setWrite(AccessList write) {
		this.write = write;
	}

	public AccessList getRead() {
		return read;
	}

	public void setRead(AccessList read) {
		this.read = read;
	}

	public AccessList getDelete() {
		return delete;
	}

	public void setDelete(AccessList delete) {
		this.delete = delete;
	}
	
	public abstract String getName();
}
