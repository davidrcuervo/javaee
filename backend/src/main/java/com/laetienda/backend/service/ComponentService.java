package com.laetienda.backend.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;
import org.laetienda.backend.engine.Ldap;

import com.google.gson.Gson;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.backend.repository.ComponentRepository;
import com.laetienda.backend.repository.RepositoryInterface;
import com.laetienda.lib.mistake.MistakeDeprecated;
import com.laetienda.lib.model.Component;
import com.laetienda.lib.model.Objeto;

public class ComponentService implements SimpleService {
	private final static Logger log = LogManager.getLogger(ComponentService.class);
	
	private EntityManagerFactory emf;
	private Authorization auth;
	private Db db;
	private Gson gson;
	private Ldap ldap;
	
	
	public ComponentService(EntityManagerFactory emf, Authorization auth) {
		this.emf = emf;
		this.auth = auth;
		db = new Db();
		gson = new Gson();
		ldap = new Ldap();
	}

	@Override
	public RepositoryInterface get(String name) {
		ComponentRepository result = null;
		EntityManager em = null;
	
		try {
			em = emf.createEntityManager();
			result = new ComponentRepository(name, em, auth);
		}catch(Exception e) {
			log.warn("Failed to get component. $componentName: {} - $exception : {} -> {}", name, e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to get component.", e);
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}

	@Override
	public RepositoryInterface post(String data) {
		ComponentRepository result = null;
		EntityManager em = null;
		log.debug("test logger");
		try {
			em = emf.createEntityManager();
			Component jsonc = gson.fromJson(data, Component.class); 
			Objeto clazz = (Objeto) Class.forName(jsonc.getJavaClassName()).getConstructor().newInstance();
			User owner = ldap.findUser(jsonc.getOwner(), auth.getLdapConnection());
			Group group = ldap.findGroup(jsonc.getGroup(), auth.getLdapConnection());
			AccessListRepository readR = new AccessListRepository(jsonc.getRead().getName(), em, auth);
			AccessListRepository writeR = new AccessListRepository(jsonc.getWrite().getName(), em, auth);
			AccessListRepository deleteR = new AccessListRepository(jsonc.getDelete().getName(), em, auth);
			
			result = new ComponentRepository(
					jsonc.getName(), jsonc.getDescription(), clazz.getClass(), 
					owner, group, 
					readR, writeR, deleteR, 
					em, auth.getLdapConnection()
					);
			
			if(db.insert(result, em, auth)) {
				db.commit(em, auth);
			}
			
		}catch(Exception e) {
			if(result != null)result.addError(500, "Component", "Internal Error", "$exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
			log.warn("Failed to insert new component. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to insert new component", e);
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}

	@Override
	public RepositoryInterface put(String componentJson) {
		ComponentRepository result = null;
		EntityManager em = null;
		
		try {
			Component jsonc = gson.fromJson(componentJson, Component.class);
			em = emf.createEntityManager();
			db.begin(em);
			
			result = new ComponentRepository(jsonc.getName(), em, auth);

			if(auth.canWrite(result.getObjeto())) {				
				result.merge(jsonc, em, auth);
				if(result.getErrors().size() > 0) {
					log.debug("Componet has erros and can't be persisted. $erros: {}", result.getErrors().size());
					if(log.isDebugEnabled()) {
						for(MistakeDeprecated error : result.getErrors()) {
							log.debug(
									"$error -> $status: {}, $pointer: {}, $title: {}, detail: {}", 
									error.getStatus(),
									error.getPointer(),
									error.getTitle(),
									error.getDetail()
									);
						}
					}
				}else {
					db.commit(em, auth);
					log.info("Component has updated succesfully");
				}
				
			}else {
				result.addError(401, "Component", "Invalid Auhtorization", "User does not have enough privileges to apply changes to Component");
			}
			
		}catch(Exception e) {
			if(result !=null)result.addError(500, "Component", "Internal Exception", "$Exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
			log.warn("Failed to put (update) component. $Exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to put (update) component." ,e);
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}

	@Override
	public boolean delete(String name) {
		boolean result = false;
		
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			db.begin(em);
			ComponentRepository compR = new ComponentRepository(name, em, auth);
			
			if(db.remove(compR.getObjeto(), em, auth)) {
				db.commit(em, auth);
				result = true;
			}
			
		}catch(Exception e) {
			log.warn("Failed to remove Component. $Exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to remove Component.", e);			
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}

}
