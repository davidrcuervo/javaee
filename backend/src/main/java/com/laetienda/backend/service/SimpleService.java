package com.laetienda.backend.service;

import com.laetienda.backend.repository.RepositoryInterface;

public interface SimpleService {
	
	public RepositoryInterface get(String name);
	public RepositoryInterface post(String data);
	public RepositoryInterface put(String jsonData);
	public boolean delete(String name);

}
