package com.laetienda.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.myldap.Group;
import com.laetienda.myldap.User;

@Entity
@Table(name="components")
@NamedQueries({
		@NamedQuery(name="Component.findAll", query="SELECT c FROM Component c"),
		@NamedQuery(name="Component.findByName", query="SELECT c FROM Component c WHERE c.name = :name"),
		@NamedQuery(name="Component.findByJavaClassName", query="SELECT c FROM Component c WHERE c.javaClassName = :javaClassName")
		})
public class Component extends Objeto implements Serializable {
	private static final Logger log = LogManager.getLogger(Component.class);
	private static final long serialVersionUID = 1L;

	@Column(name="\"name\"", unique=true, nullable=false, length=254)
	private String name;
	
	@Column(name="\"java_class_name\"", unique=true, nullable=false, length=254)
	private String javaClassName;

	@Column(name="\"description\"", unique=false, length=254)
	private String description;
	
	public Component() {
		
	}
	
	public Component(String name, String description, Class<?> javaClass,User owner, Group group, AccessList delete, AccessList write, AccessList read, EntityManager em, LdapConnection conn) throws Exception {
		super(owner, group, delete, write, read, conn);
		setName(name, em);
		setDescription(description);
		setJavaClass(javaClass);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		
		if(description == null || description.isEmpty()) {
			log.warn("Failed to set component description, it can't be empty.");
			addError("Description", "Failed to set Access List description, it can't be empty.");
		}else if(name.length() > 254) {
			log.warn("Failed to set Access List description, it can't have more than 254 characters.");
			addError("Description", "Failed to set Access List description, it can't have more than 254 characters.");
		}else {
			log.debug("Description of component has set succesfully with no error. $description: {}", this.description);
		}
	}

	private void setName(String name, EntityManager em) {
		this.name = name;
		
		if(name == null || name.isEmpty()) {
			log.warn("Failed to set Component name, it can't be empty");
			addError("Name", "Failed to set Component Name, it can't be empty");
		}else if(name.length() > 254) {
			log.warn("Failed to set Component Name, it can't have more than 254 characters.");
			addError("Name", "Failed to set Access List Name, it can't have more than 254 characters.");
		}else {
			List<Component> result = em.createNamedQuery("Component.findByName", Component.class).setParameter("name", name).getResultList();
			
			if(result.size() > 0) {
				log.warn("Failed to set Component name, it can't be empty");
				addError("Name", "Failed to set Component Name, Another component with same name already exists");
			}else {
				log.debug("Component name has been set succesfully (no errors). $ComponentName: " + this.name);
			}
		}
	}
	
	public String getJavaClassName() {
		return javaClassName;
	}

	private void setJavaClass(Class<?> javaClass) {
		
		
		try {
			Object o = Class.forName(javaClass.getName()).getDeclaredConstructor().newInstance();
			if(o instanceof Objeto) {
				this.javaClassName = javaClass.getName();
				log.debug("javaClassName has be set succesfully with no errors");
			}else {
				addError("javaClassName", "java class name must be instance of Objeto.");
				log.warn("Failed to set javaClassName. java class name must be instance of Objeto.");
			}
		}catch(Exception e) {
			addError("javaClassName", "java class name can't not be set. The java class does not exist");
			log.warn("Failed to set javaClassName. The java class does not exist. $Exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to set javaClassName", e);
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
