package com.laetienda.backend.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;
import org.laetienda.backend.engine.Ldap;

import com.google.gson.Gson;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.AccessListRepository;
import com.laetienda.backend.repository.RepositoryInterface;
import com.laetienda.lib.model.AccessList;

public class AccessListService implements SimpleService {
	private final static Logger log = LogManager.getLogger(AccessListService.class);
	
	private EntityManagerFactory emf;
	private Authorization auth;
	private Db db;
	private Gson gson;
	private Ldap ldap;
	
	public AccessListService(EntityManagerFactory emf, Authorization auth) {
		this.emf = emf;
		this.auth = auth;
		db = new Db();
		gson = new Gson();
		ldap = new Ldap();
	}

	@Override
	public RepositoryInterface get(String name) {
		AccessListRepository result = null;
		
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			result = new AccessListRepository(name, em, auth);

		}catch(IllegalStateException | IOException e) {
			log.warn("Failed to get Access List. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to get Access List", e);
		}finally {
			db.closeEm(em);
		}

		return result;
	}



	@Override
	public RepositoryInterface post(String data) {
		
		AccessListRepository result = null;
		EntityManager em = null;
		try {
			
			AccessList acl = gson.fromJson(data, AccessList.class);
			
			User user = ldap.findUser(acl.getOwner(), auth.getLdapConnection());
			Group group = ldap.findGroup(acl.getGroup(), auth.getLdapConnection());

			em = emf.createEntityManager();
			db.begin(em);
			
			result = new AccessListRepository(
					acl.getName(), acl.getDescription(), 
					user, group, 
					em, auth.getLdapConnection());
			
			if(acl.getDelete() !=null) {
				result.setDelete(new AccessListRepository(acl.getDelete().getName(), em, auth));
			}
			
			if(acl.getWrite() != null) {
				result.setWrite(new AccessListRepository(acl.getWrite().getName(), em, auth));
			}
			
			if(acl.getRead() != null) {
				result.setRead(new AccessListRepository(acl.getRead().getName(), em, auth));
			}
			
			if(db.insert(result, em, auth)) {
				 db.commit(em, auth);
			}
			
		}catch(Exception e) {
			if(result !=null)result.addError(500, "Access List", "Internal Server Error", "$exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
			log.warn("Failed to save Access List into database. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to save Access List into database.", e);
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}

	@Override
	public RepositoryInterface put(String json) {
		
		AccessListRepository result = null;
		EntityManager em = null;

		try {
			AccessList aclJson = gson.fromJson(json, AccessList.class);
			em = emf.createEntityManager();
			db.begin(em);
			result = new AccessListRepository(aclJson.getName(), em, auth);
			
			if(auth.canWrite(result.getObjeto())) {
				result.merge(aclJson, em, auth);
				if(result.getErrors().size() > 0) {
					
				}else {
					db.commit(em, auth);
				}
			}else {
				result.addError(401, "Access List", "Unathorized", "User does not have enough privileges to apply changes to Access List");
			}
		
		}catch(Exception e) {
			if(result != null)result.addError(500, "Access List", "Internal Exception", "$exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
			log.warn("Failed to update Access List. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to update Access List.", e);
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}

	@Override
	public boolean delete(String aclName) {
		boolean result = false;
		
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			db.begin(em);
			TypedQuery<?> query = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", aclName);
			AccessList acl = (AccessList) db.find(query, em, auth);
			
			if(db.remove(acl, em, auth)) {				
				db.commit(em, auth);
				result = true;
			}
		}catch(Exception e) {
			log.warn("Failed to remove AccessList. $Exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to remove AccessList.", e);
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}
}
