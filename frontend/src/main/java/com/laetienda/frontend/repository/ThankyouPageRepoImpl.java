package com.laetienda.frontend.repository;

import java.util.HashMap;
import java.util.Map;

import com.laetienda.model.webdb.ThankyouPage;

public class ThankyouPageRepoImpl implements ThankyouPageRepository {

	private Map<String, ThankyouPage> dbThankyouPage;
	
	public ThankyouPageRepoImpl() {
		dbThankyouPage = new HashMap<String, ThankyouPage>();
	}
	
	public ThankyouPageRepoImpl(Map<String, ThankyouPage> dbThankyouPage) {
		this.dbThankyouPage = dbThankyouPage;
	}

	@Override
	public ThankyouPage find(String key) {
		return dbThankyouPage.get(key);
	}
	
	@Override
	public synchronized void addThankyouPage(String key, ThankyouPage thankyoupage) {
		dbThankyouPage.put(key, thankyoupage);
	}

	@Override
	public synchronized void removeThankyouPage(String key) {
		dbThankyouPage.remove(key);
	}

	@Override
	public boolean isValid(String key) {
		boolean result = false;
		
		ThankyouPage thkpage = dbThankyouPage.get(key);
		
		if(thkpage != null && thkpage.getKey().equals(key)){
			result = true;
		}
		
		return result;
	}
}
