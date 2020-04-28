package com.laetienda.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.backend.repository.ObjetoRepository;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Objeto;

@Entity
@Table(name="forms")
@NamedQueries({
	@NamedQuery(name="Form.findall", query="SELECT f FROM Form f"),
	@NamedQuery(name="Form.findByName", query="SELECT f FROM Form f where f.name = :name")
})
public class Form extends Objeto implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(Form.class);
	
	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"clase\"", unique=false, nullable=false, length=254)
	private String clase;
	
	/*
	@Column(name="\"submit\"", unique=false, nullable=false, length=254)
	private String submit;
	*/
	@Column(name="\"email\"", unique=false, nullable=true, length=254)
	private String email;
	
	@Column(name="\"thankyou\"", unique=false, nullable=true, length=254)
	private String thankyou;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="form", orphanRemoval=true)
	private List<Input> inputs = new ArrayList<Input>();
	
	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=false)
	private AccessList canCreateAcl;
	
	public Form() {
		super();
	}
	
	public Form(String name, String clase/*, String submit*/) {
		super();
		this.name = name;
		this.clase = clase;
		//this.submit = submit;
	}

	/**
	 * 
	 * @param name Name or identifier of the form, must be unique
	 * @param clase the entity java class that will persist data into db
	 * @param email path to jsp email format that will send on success completation form
	 * @param thankyou path to jsp thank you html page that will be displayed on cusccess completation form
	 * @param acl ACL object that describes the permsions of the form
	 */
	public Form(String name, String clase, /*String submit,*/ String email, String thankyou, AccessListRepository acl) {
		super();
//		this.setName(name);
//		this.setClase(clase);
//		this.setEmail(email);
//		//this.submit = submit;
//		this.setThankyou(thankyou);
//		this.setCanCreateAcl(acl);
	}

	@Override
	public String getName() {
		
		return name;
	}



}
