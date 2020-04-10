package com.laetienda.dbentities;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name="inputs")
@NamedQueries({
	@NamedQuery(name="Input.findall", query="SELECT i FROM Input i")
})
public class Input implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(Input.class);
	
	@Id
	@SequenceGenerator(name = "input_id_seq", sequenceName = "input_id_seq", allocationSize=1)
	@GeneratedValue(generator = "input_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name="\"id\"", updatable=false, nullable=false, unique=true)
	private Integer id;
	
	@ManyToOne
	private Form form;
	
	@Column(name="\"name\"", unique=false, nullable=false, length=254)
	private String name;
	
	@Column(name="\"label\"", unique=false, nullable=false, length=254)
	private String label;
	
	@Column(name="\"type\"", unique=false, nullable=false, length=254)
	private String type;
	
	@Column(name="\"placeholder\"", unique=false, nullable=true, length=254)
	private String placeholder;
	
	@Column(name="\"glyphicon\"", unique=false, nullable=true, length=254)
	private String glyphicon;
	
	@Column(name="\"value\"", unique=false, nullable=false, length=254)
	private boolean value;
	
	
	public Input() {
		super();
	}

	public Input(Form form, String name, String label, String type, boolean value) {
		super();
		this.form = form;
		this.name = name;
		this.label = label;
		this.type = type;
		this.value = value;
	}

	public Input(Form form, String name, String label, String type, String placeholder, String glyphicon,
			boolean value) {
		super();
		this.form = form;
		this.name = name;
		this.label = label;
		this.type = type;
		this.placeholder = placeholder;
		this.glyphicon = glyphicon;
		this.value = value;
	}

	public Form getForm() {
		return form;
	}



	public void setForm(Form form) {
		this.form = form;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getLabel() {
		return label;
	}



	public void setLabel(String label) {
		this.label = label;
	}



	public String getType() {
		return type.toLowerCase();
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getPlaceholder() {
		return placeholder;
	}



	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}



	public String getGlyphicon() {
		return glyphicon;
	}



	public void setGlyphicon(String glyphicon) {
		this.glyphicon = glyphicon;
	}



	public boolean isValue() {
		return value;
	}



	public void setValue(boolean value) {
		this.value = value;
	}



	public static void main(String[] args) {
		log.info("Testing Input form");

	}

}
