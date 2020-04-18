package com.laetienda.backend.repository;

import java.util.List;

import javax.persistence.*;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.model.Component;

public class ComponentRepository extends ObjetoRepository implements RepositoryInterface {
	private static final Logger log = LogManager.getLogger(ComponentRepository.class);

	private Component component;
	
	public ComponentRepository() {
		component = new Component();
	}
	
	public ComponentRepository(String name, String description, Class<?> javaClass,User owner, Group group, AccessListRepository delete, AccessListRepository write, AccessListRepository read, EntityManager em, LdapConnection conn) throws Exception {
		super(owner, group, delete, write, read, conn);
		component = new Component();
		setName(name, em);
		setDescription(description);
		setJavaClass(javaClass);
	}
	
	public String getDescription() {
		return component.getDescription();
	}

	public void setDescription(String description) {
		component.setDescription(description);
		
		if(description == null || description.isEmpty()) {
			log.warn("Failed to set component description, it can't be empty.");
			addError("Description", "Failed to set Access List description, it can't be empty.");
		}else if(description.length() > 254) {
			log.warn("Failed to set Access List description, it can't have more than 254 characters.");
			addError("Description", "Failed to set Access List description, it can't have more than 254 characters.");
		}else {
			log.debug("Description of component has set succesfully with no error. $description: {}", description);
		}
	}

	private void setName(String name, EntityManager em) {
		component.setName(name);
		
		if(name == null || name.isEmpty()) {
			log.warn("Failed to set Component name, it can't be empty");
			addError("Name", "Failed to set Component Name, it can't be empty");
		}else if(name.length() > 254) {
			log.warn("Failed to set Component Name, it can't have more than 254 characters.");
			addError("Name", "Failed to set Access List Name, it can't have more than 254 characters.");
		}else {
			List<ComponentRepository> result = em.createNamedQuery("Component.findByName", ComponentRepository.class).setParameter("name", name).getResultList();
			
			if(result.size() > 0) {
				log.warn("Failed to set Component name, it can't be empty");
				addError("Name", "Failed to set Component Name, Another component with same name already exists");
			}else {
				log.debug("Component name has been set succesfully (no errors). $ComponentName: " + name);
			}
		}
	}
	
	public String getJavaClassName() {
		return component.getJavaClassName();
	}

	private void setJavaClass(Class<?> javaClass) {
		
		
		try {
			Object o = Class.forName(javaClass.getName()).getDeclaredConstructor().newInstance();
			if(o instanceof ObjetoRepository) {
				component.setJavaClassName(javaClass.getName());
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
		return component.getName();
	}
}
