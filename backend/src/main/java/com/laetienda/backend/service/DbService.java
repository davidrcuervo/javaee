package com.laetienda.backend.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;

import com.laetienda.backend.json.DbInputJsonParser;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.repository.ObjetoRepository;

public class DbService {
	private static final Logger log = LogManager.getLogger(DbService.class);
	
	private Db db;
	private EntityManagerFactory emf;
	private Authorization auth;
	
	public DbService(EntityManagerFactory emf, AuthTables tables, Authorization auth) {
		this.emf = emf;
		this.auth = auth;
		db = new Db();
	}
	
	/**
	 * 
	 * @param className only simple name. It will prepend "com.laetienda.mode" to classname
	 * @param queryName 
	 * @param parameters
	 * @return Null if parameters are wrong
	 */
	public ObjetoRepository find(String className, String queryName, Map<String,String> parameters) {
		log.info("Searching object in database. $className: {} - $queryName: {}", className, queryName);
		
		ObjetoRepository result = null;
		
		TypedQuery<?> query;
		EntityManager em = null;
		
		try {
			ObjetoRepository obj = (ObjetoRepository)Class.forName("com.laetienda.model.AccessList").getConstructor().newInstance();
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
	
	public ObjetoRepository add(List<DbInputJsonParser> parameters){
		ObjetoRepository result = null;
		
		
		
		EntityManager em = emf.createEntityManager();
		
		return result;
	}
	
//	private Component getComponent(dbJsonParser dbJson) {
//	
//		Component result = null;
//		EntityManager em = null;
//		
//		try {
//			em = emf.createEntityManager();
//			qComponent = em.createNamedQuery("Component.findByName", Component.class).setParameter("name", dbJson.getComponent());
//			result = (Component)db.find(qComponent, em, auth);
//		}catch(IllegalStateException e) {
//			
//		}finally {
//			db.closeEm(em);
//		}
//		
//		return result;
//	}
	
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
