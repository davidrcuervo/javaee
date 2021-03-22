package com.laetienda.webdb.repository;

import java.util.List;

import com.laetienda.lib.model.AccessList;

public interface AccessListRepository {
	
	public AccessList findById(int id);
	public List<AccessList> findAll();
	public boolean canRead(String uid);
	public boolean canWrite(String uid);
	public boolean canDelete(String uid);
	
}
