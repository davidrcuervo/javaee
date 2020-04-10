package com.laetienda.dbentities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	public Form(String name, String clase, /*String submit,*/ String email, String thankyou, AccessList acl) {
		super();
		this.setName(name);
		this.setClase(clase);
		this.setEmail(email);
		//this.submit = submit;
		this.setThankyou(thankyou);
		this.setCanCreateAcl(acl);
	}

	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getClase() {
		return clase;
	}



	public void setClase(String clase) {
		this.clase = clase;
	}


/*
	public String getSubmit() {
		return submit;
	}



	public void setSubmit(String submit) {
		this.submit = submit;
	}
*/


	public String isEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getThankyou() {
		return thankyou;
	}



	public void setThankyou(String thankyou) {
		this.thankyou = thankyou;
	}
	
	public List<Input> getInputs(){
		return inputs;
	}

	public Form addInput(Input input) {
		
		input.setForm(this);
		inputs.add(input);
		
		return this;
	}

	public AccessList getCanCreateAcl() {
		return canCreateAcl;
	}

	public void setCanCreateAcl(AccessList acl) {
		this.canCreateAcl = acl;
	}

	public static void main(String[] args) {
		log.info("Hello Form World!");

	}

}
