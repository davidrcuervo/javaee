package com.laetienda.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.laetienda.backend.repository.ObjetoRepository;
import com.laetienda.lib.model.Objeto;

@Entity
@Table(name="variables")
@NamedQueries({
	@NamedQuery(name="Variable.findall", query="SELECT v FROM Variable v"),
	@NamedQuery(name="Variable.findByName", query="SELECT v FROM Variable v WHERE v.name = :name")
})

public class Variable extends Objeto implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/*
	@Id
	@SequenceGenerator(name = "variable_id_seq", sequenceName = "variable_id_seq", allocationSize=1)
	@GeneratedValue(generator = "variable_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name="\"id\"", updatable=false, nullable=false, unique=true)
	private Integer id;
	
	@OneToOne (cascade=CascadeType.ALL)
	@JoinColumn(name="object_id", unique=true, nullable=false, updatable=false, insertable=true)
	private Objeto objeto;
	*/
	
	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"description\"", unique=true, nullable=false, length=254)
	private String description;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="variable", orphanRemoval=true)
	@JoinColumn(name="id")
	private List<Option> options = new ArrayList<Option>();
	
	public Variable() {
		
	}
	
	public Variable(String name, String description) {
		setName(name);
		setDescription(description);
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

	public List<Option> getOptions() {
		return options;
	}
	
	public Option addOption(Option option) {
		
		option.setVariable(this);
		//TODO Validate that option does not exist
		options.add(option);
		return option;
	}
	
	public Option addOption(String name, String description) {
		Option option = new Option(name, description);
		addOption(option);
		
		return option;
	}
	
	/*
	public Objeto getObjeto() {
		return objeto;
	}

	public void setObjeto(Objeto objeto) {
		this.objeto = objeto;
	}
	*/
}
