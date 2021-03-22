package com.laetienda.webdb.repository;

import javax.persistence.EntityManager;

import com.laetienda.model.webdb.DbRow;

public class WebDbRepoImpl implements WebDbRepository {

	public WebDbRepoImpl() {
		
	}

	public void insert(DbRow row, EntityManager em, String uid) {
		em.persist(row);
	}

	public void select(DbRow row, EntityManager em, String uid) {
		
	}

	public void update(DbRow row, EntityManager em, String uid) {

	}

	public void delete(DbRow row, EntityManager em, String uid) {
		
	}
}
