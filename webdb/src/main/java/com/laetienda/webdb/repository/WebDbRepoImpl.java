package com.laetienda.webdb.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import com.laetienda.lib.http.HttpClientException;
import com.laetienda.model.api.UserApi;
import com.laetienda.model.webdb.DbRow;
import com.laetienda.model.webdb.Usuario;

public class WebDbRepoImpl implements WebDbRepository {

	enum Accion {INSERT, UPDATE};

	private EntityManagerFactory emf;
	private Usuario visitor;
	private UserApi api;
	
	public WebDbRepoImpl() {
		
	}
	
	public WebDbRepoImpl(String visitor, EntityManagerFactory emf) throws HttpClientException {
		setEntityManagerFactory(emf);
		setVisitor(visitor);
	}
	
	public WebDbRepoImpl setVisitor(String visitor) throws HttpClientException {
		this.visitor = api.getUser(visitor);
		return this;
	}
	
	public WebDbRepoImpl setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
		return this;
	}

	public void insert(DbRow row) {
		inupdate(row, Accion.INSERT);
	}
	
	public void update(DbRow row) {

	}
	
	private void inupdate(DbRow row, Accion act) {
		
		EntityManager em = emf.createEntityManager();
		
		try {
			
		}catch(PersistenceException e) {
			
		}finally {
			em.close();
		}
	}

	public void select(DbRow row) {
		
	}



	public void delete(DbRow row) {
		
	}
	
	private boolean canWrite(DbRow row, Usuario visitor) {
		
		
		
		return false;
	}
	
	private boolean canWrite(DbRow row) {
		return canWrite(row, visitor);
	}
}
