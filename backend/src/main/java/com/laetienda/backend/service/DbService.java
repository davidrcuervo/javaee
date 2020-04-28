package com.laetienda.backend.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;
import org.laetienda.backend.engine.Ldap;

import com.google.gson.Gson;
import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.repository.ObjetoRepository;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Objeto;

public class DbService {
	private static final Logger log = LogManager.getLogger(DbService.class);
	
	private Db db;
	private Ldap ldap;
	private EntityManagerFactory emf;
	private Authorization auth;
	private Gson gson;
	private LdapConnection connTomcat;
	
	public DbService(EntityManagerFactory emf, Authorization auth, LdapConnection connTomcat) {
		this.emf = emf;
		this.auth = auth;
		this.connTomcat = connTomcat;
		db = new Db();
		ldap = new Ldap();
		gson = new Gson();
	}
	
	/**
	 * 
	 * @param className only simple name. It will prepend "com.laetienda.mode" to classname
	 * @param queryName 
	 * @param parameters
	 * @return Null if parameters are wrong
	 */
	public Objeto find(String className, String queryName, Map<String,String> parameters) {
		log.info("Searching object in database. $className: {} - $queryName: {}", className, queryName);
		
		Objeto result = null;
		
		TypedQuery<?> query;
		EntityManager em = null;
		
		try {
			Objeto obj = (Objeto)Class.forName("com.laetienda.lib.model." + className).getConstructor().newInstance();
			em = emf.createEntityManager();
			query = em.createNamedQuery(queryName, obj.getClass());
			
			for(Map.Entry<String, String> parameter : parameters.entrySet()) {
				query = query.setParameter(parameter.getKey(), parameter.getValue());
			}
			
			result = db.find(query, em, auth);
			if(result != null)log.info("object found succesfully. $name: {}", result.getName());			
		}catch(Exception e) {
			log.warn("Failed to search object. $Exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to search object." , e);
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}
	
	public Objeto post(String className, String jsonObjeto){
		Objeto result = null;
		EntityManager em = null;
		User owner;
		Group group;
		AccessList aclDelete, aclWrite, aclRead;
		
		try {
			em = emf.createEntityManager();
			db.begin(em);
			Objeto clazz = (Objeto)Class.forName("com.laetienda.lib.model." + className).getConstructor().newInstance();
			Objeto temp = gson.fromJson(jsonObjeto, clazz.getClass());
			
			log.debug((temp == null ? "It was not able to build object from json." : "Object succesfully found from json"));
			
			aclDelete = em.find(AccessList.class, temp.getDelete().getId());
			aclWrite = em.find(AccessList.class, temp.getWrite().getId());
			aclRead = em.find(AccessList.class, temp.getRead().getId());
			owner = ldap.findUser(temp.getOwner(), connTomcat);
			group = ldap.findGroup(temp.getGroup(), connTomcat);
			
			if(aclDelete == null && aclWrite == null || aclRead == null || owner == null || group == null ) {
				log.warn("Components don't exist to persist object request");
			}else {
				
				temp.setDelete(aclDelete);
				temp.setWrite(aclWrite);
				temp.setRead(aclRead);
				
				if(db.insert(temp, em, auth)) {
					db.commit(em, auth);
					result = em.find(clazz.getClass(), temp.getId());
				}
			}
			
		}catch(Exception e) {
			log.warn("Failed to save request into the database. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to save request into the database.", e);
		}finally {
			db.closeEm(em);
		}
				
		return result;
	}
	
	public Objeto put(String className, String jsonData) {
		Objeto result = null;
		
		EntityManager em = null;
		
		try {
			em = emf.createEntityManager();
			db.begin(em);
			Objeto clazz = (Objeto)Class.forName("com.laetienda.lib.model." + className).getConstructor().newInstance();
			Objeto json = gson.fromJson(jsonData, clazz.getClass());
			db.merge(json, em, auth);
			db.commit(em, auth);

			result = (Objeto)em.find(clazz.getClass(), json.getId());
		}catch(Exception e) {
			log.warn("Failed to update instance of the database. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to update instance of the database.", e);
		}finally {
			db.closeEm(em);
		}
		
		return result;
	}
	
	public boolean delete(String className, int id) {
		boolean result = false;
		
		return result;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ObjetoRepository o = (ObjetoRepository)Class.forName("com.laetienda.model.AccessList").getConstructor().newInstance();
		
//		Object o = clazz.getConstructor().newInstance();
		log.debug("$o.getClass().getName(): {}", o.getClass().getName());
		o.getClass().getMethods();
		for(Method method : o.getClass().getMethods()) {
			method.getName();
		}
	}
}
