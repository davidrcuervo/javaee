package com.laetienda.webdb.repository;

import javax.persistence.EntityManager;

import com.laetienda.model.webdb.DbRow;

public interface WebDbRepository {
	
	public void insert(DbRow row, EntityManager em, String uid);
	public void select(DbRow row, EntityManager em, String uid);
	public void update(DbRow row, EntityManager em, String uid);
	public void delete(DbRow row, EntityManager em, String uid);

}
