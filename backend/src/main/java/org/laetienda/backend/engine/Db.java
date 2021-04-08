package org.laetienda.backend.engine;

import static com.laetienda.backend.myapptools.Ajustes.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.backend.repository.RepositoryInterface;
import com.laetienda.lib.model.Component;
import com.laetienda.lib.model.Objeto;
import com.laetienda.lib.utilities.AesFirstRepoImpl;

public class Db {
	final private static Logger log = LogManager.getLogger(Db.class); 
	
	/**
	 * 
	 * @param persistenceUnitName check persistence.xml file.
	 * @param password
	 * @return
	 */
	@Deprecated
	public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, String password) {
		
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("javax.persistence.jdbc.password", password);
		return Persistence.createEntityManagerFactory(persistenceUnitName, properties);
	}
	
	public EntityManagerFactory createEntityManagerFactory() throws GeneralSecurityException {
		String password = new AesFirstRepoImpl().decrypt(DB_AES_PASSWORD, DB_USERNAME);
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("javax.persistence.jdbc.password", password);
		return Persistence.createEntityManagerFactory(DB_PERSISTENCE_UNIT_NAME, properties);
	}
	
	public void closeEmf(EntityManagerFactory emf) {
		
		try {
			emf.close();
		}catch (NullPointerException | IllegalStateException e) {
			log.warn("Failed to close Entity Manager Factory. $error: {}", e.getMessage());
			log.debug("Failed to close Entity Manager Factory.", e);
		}
	}
	
	public void closeEm(EntityManager em) {
		
		try {
			em.close();
		}catch (NullPointerException | IllegalStateException e) {
			log.warn("Failed to close Entity Manager. $error: {}", e.getMessage());
			log.debug("Failed to close Entity Manager.", e);
		}
	}
		
	public void begin(EntityManager em) throws PersistenceException  {
		
		try {
			if(!em.getTransaction().isActive()) {
				em.getTransaction().begin();
			}
		}catch (PersistenceException  e) {
			log.warn("Failed to start transaction. $error: {}", e.getMessage());
			log.debug("Failed to start transaction.", e);
			throw e;
		}
	}
	
	public Objeto find(TypedQuery<?> query, EntityManager em, Authorization auth) {
		
		Objeto result = null;
		try {
			result = (Objeto)query.getSingleResult();
			
			if(auth.canRead(result)) {
				
				log.debug("$user: {} -> can read $objeto: {}", auth.getUser().getUid(), result.getName());
			}else {
				log.debug("$user: {} -> does not have rights to read $objeto: {}", auth.getUser().getUid(), result.getName());
				result = null;
			}
			
		}catch(Exception e) {
			log.warn("Failed to find objeto. $exception {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to find objeto.", e);
		}
		
		return result;
	}
	
	public boolean insert(RepositoryInterface objRepo, EntityManager em, Authorization auth) throws Exception {
		boolean result = false;
		
		if(objRepo.getErrors().size() > 0) {
			log.warn("It contains errors and can't be persisted into database");
		}else {
			result = insert(objRepo.getObjeto(), em, auth);
		}
		
		return result;
	}
	
	public boolean insert(Objeto obj, EntityManager em, Authorization auth) throws Exception {
		log.debug("obj.getClass().getName(): {}", obj.getClass().getName());	
		boolean result = false;
		
		List<Component> comps = em.createNamedQuery("Component.findByJavaClassName", Component.class).setParameter("javaClassName", obj.getClass().getName()).getResultList();
		Component comp = null;
		
		if(comps != null && comps.size() == 1) {
			comp = comps.get(0);
		}
	
//		if(auth.isInstallFlag() || auth.canWrite(comp)) {
		if(auth.isSysadmin() || auth.canWrite(comp)) {
			result = insert(obj, em);
		}else {
			log.warn("{} is not authorizes to create", auth.getUser().getUid());
		}
		
		return result;
	}
	
	private boolean insert(Objeto obj, EntityManager em) throws PersistenceException, IllegalArgumentException{
		log.debug("Inserting objeto in database");
		
		boolean result = false;
		try {
			
//			if(obj.getErrors().size() > 0) {
//				log.warn("It contains errors and can't be persisted into database");				
//			}else {
				
				if(em.getTransaction().isActive()) {
					log.debug("EntityManager transaction is active");					
				}else {
					log.debug("Starting EntintyManager transaction");
					em.getTransaction().begin();
				}
				
				em.persist(obj);
				result = true;
//			}
			
		}catch (PersistenceException | IllegalArgumentException e) {
			log.error("Failed to persist in database. $error: {}", e.getMessage());
			throw e;
		}
		
		return result;
	}
	
	public boolean merge(Objeto obj, EntityManager em, Authorization auth) throws LdapException {
		boolean result = false;
		
		if(auth.canWrite(obj)) {
			em.merge(obj);
			result = true;
		}
		
		return result;
	}
	
	public boolean remove(Objeto obj, EntityManager em, Authorization auth) throws LdapException {
		boolean result = false;
		if(auth.canDelete(obj)) {
			if(em.getTransaction().isActive()) {
				log.debug("EntityManager transaction is active");					
			}else {
				log.debug("Starting EntintyManager transaction");
				em.getTransaction().begin();
			}
			
			em.remove(obj);
			result=true;
		}else {
			
		}
		
		return result;
	}
	
	public void commit(EntityManager em, Authorization auth) throws RuntimeException{
		try {
			em.getTransaction().commit();
		}catch(RollbackException e) {
			log.error("Failed to commit changes to database. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to commit changes to database", e);
			rollback(em);
			throw e;
		}catch(IllegalStateException e) {
			log.error("Failed to commit changes to database. $error: {}", e.getMessage());
			log.debug("Failed to commit changes to database", e);
			throw e;
		}
	}
	
	public void rollback(EntityManager em) {
		
		try {
			if(em.getTransaction().getRollbackOnly()) {
				em.getTransaction().rollback();
			}
		}catch(Exception e) {
			log.error("It has failed to rollback. $error: {}", e.getMessage());
			log.debug("Failed to rollback.", e);
		}
	}
}
