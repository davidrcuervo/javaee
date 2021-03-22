package com.laetienda.model.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.latienda.model.utils.Validate;

@Entity
@Table(name="product")
@NamedQueries({
	@NamedQuery(name="Product.findall", query="SELECT p FROM Product p"),
	@NamedQuery(name="Product.findByName", query="SELECT p FROM Product p WHERE p.name = :name")
})

public class Product {
	
	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Transient
	private Validate validate;
	
	public Product() {
		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	

}
