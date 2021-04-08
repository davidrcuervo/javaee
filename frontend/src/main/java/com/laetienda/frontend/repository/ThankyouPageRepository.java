package com.laetienda.frontend.repository;

import com.laetienda.model.webdb.ThankyouPage;

public interface ThankyouPageRepository {
	
	public ThankyouPage find(String key);
	public void addThankyouPage(String key, ThankyouPage thankyoupage);
	public void removeThankyouPage(String key);
	public boolean isValid(String key);
}
