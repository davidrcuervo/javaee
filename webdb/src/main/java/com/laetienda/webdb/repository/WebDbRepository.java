package com.laetienda.webdb.repository;

import com.laetienda.model.webdb.DbRow;

public interface WebDbRepository {
	
	public void insert(DbRow row);
	public void select(DbRow row);
	public void update(DbRow row);
	public void delete(DbRow row);

}
