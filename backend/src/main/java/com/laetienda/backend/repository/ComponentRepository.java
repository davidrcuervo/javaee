package com.laetienda.backend.repository;

import java.io.IOException;
import java.util.List;

import javax.persistence.*;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;

import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.model.Component;
import com.laetienda.lib.model.Objeto;

public class ComponentRepository extends ObjetoRepository implements RepositoryInterface {
	private static final Logger log = LogManager.getLogger(ComponentRepository.class);

	private Component component;
	private Db db;
	
	public ComponentRepository(Component component) {
		this.component = component;
	}
	
	public ComponentRepository(String name, String description, Class<?> javaClass,User owner, Group group, AccessListRepository delete, AccessListRepository write, AccessListRepository read, EntityManager em, LdapConnection conn) throws Exception {
		component = new Component();
		createObjeto(component, owner, group, delete, write, read, conn);
		setName(name, em);
		setDescription(description);
		setJavaClass(javaClass);
	}
	
	public ComponentRepository(String name, EntityManager em, Authorization auth) throws IOException {
		db = new Db();
		TypedQuery<?> query = em.createNamedQuery("Component.findByName", Component.class).setParameter("name", name.toLowerCase());
		
		Component c = (Component) db.find(query, em, auth);
		
		if(c == null) {
			throw new IOException("Component does not exist. $componentName: " + name);
		}else {
			this.setObjeto(c);
		}
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
		component.setName(name.toLowerCase());
		
		if(name == null || name.isEmpty()) {
			log.warn("Failed to set Component name, it can't be empty");
			addError("Name", "Failed to set Component Name, it can't be empty");
		}else if(name.length() > 254) {
			log.warn("Failed to set Component Name, it can't have more than 254 characters.");
			addError("Name", "Failed to set Access List Name, it can't have more than 254 characters.");
		}else {
			List<ComponentRepository> result = em.createNamedQuery("Component.findByName", ComponentRepository.class).setParameter("name", name.toLowerCase()).getResultList();
			
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
	
	public void setJavaClass(String classname) throws IOException {
		try {
			Objeto clazz = (Objeto) Class.forName(classname).getConstructor().newInstance();
			setJavaClass(clazz.getClass());
		} catch (Exception e) {
			String message = String.format("Failed to create class from classname. $classname: %s - $Exception: %s -> %s", classname, e.getClass().getSimpleName(), e.getMessage());
			log.warn(message);
			log.debug(message, e);
			throw new IOException(message);
		}
	}

	private void setJavaClass(Class<?> javaClass) {
		
		try {
			Object o = Class.forName(javaClass.getName()).getDeclaredConstructor().newInstance();
			if(o instanceof Objeto) {
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
	
	public void merge(Component jsonc, EntityManager em, Authorization auth) throws Exception {
		super.merge(jsonc, em, auth);
		
		if(jsonc.getDescription() != null) {
			setDescription(jsonc.getDescription());
		}
		
		if(!jsonc.getName().toLowerCase().equals(getName())) {
			setName(jsonc.getName(), em);
		}
		
		if(jsonc.getJavaClassName() != null) {
			setJavaClass(jsonc.getJavaClassName());
		}
	}
	

	@Override
	public String getName() {
		return component.getName();
	}
	
	public Component getObjeto() {
		return component;
	}

	@Override
	public void setObjeto(Objeto component) {
		this.component = (Component)component;
		super.setParentObjeto(this.component);
		
	}
}
