package com.laetienda.dbentities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="options")
@NamedQueries({
	@NamedQuery(name="Option.findall", query="SELECT o FROM Option o"),
	@NamedQuery(name="Option.findByName", query="SELECT o FROM Option o where o.name = :name and o.variable = :variable"),
	@NamedQuery(name="Option.findByOptionAndVariable", query="SELECT o FROM Option o WHERE o.name = :option and o.variable.name = :variable")
})

public class Option implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "option_id_seq", sequenceName = "option_id_seq", allocationSize=1)
	@GeneratedValue(generator = "option_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name="\"id\"", updatable=false, nullable=false, unique=true)
	private Integer id;
	
	/*
	@OneToOne (cascade=CascadeType.ALL)
	@JoinColumn(name="object_id", unique=true, nullable=false, updatable=false, insertable=true)
	private Objeto objeto;
	*/
	
	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"description\"", unique=true, nullable=false, length=254)
	private String description;
	
	@ManyToOne
	private Variable variable;
	
	public Option() {
		
	}
	
	public Option(String name, String description) {
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

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public Integer getId() {
		return id;
	}
	
	

}
