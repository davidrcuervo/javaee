package com.laetienda.lib.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;


/**
 * @author I849921
 *
 */
@Entity
@Table(name="forms")
@NamedQueries({
	@NamedQuery(name="Form.findall", query="SELECT f FROM Form f"),
	@NamedQuery(name="Form.findByName", query="SELECT f FROM Form f where f.name = :name")
})
public class Form extends Objeto implements Serializable {
	private static final long serialVersionUID = 1L;
	
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
	
	@Override
	public String getName() {	
		return name;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getEmail() {
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
	
	public void addInput(Input input) {
		if(inputs == null) {
			inputs = new ArrayList<Input>();
		}
		
		input.setForm(this);
		inputs.add(input);
	}

	public List<Input> getInputs() {
		return inputs;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}

	public void setName(String name) {
		this.name = name;
	}
}
