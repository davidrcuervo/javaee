package com.laetienda.myauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.myldap.User;

public class AuthTables {
	private final static Logger log = LogManager.getLogger(AuthTables.class);
	
	Map<Integer, List<String>> read = new HashMap<Integer, List<String>>();
	Map<Integer, List<String>> write = new HashMap<Integer, List<String>>();
	Map<Integer, List<String>> delete = new HashMap<Integer, List<String>>();
	
	private synchronized boolean isInTable(Map<Integer, List<String>> table, Integer id, String username) {
		boolean result = false;
		
		List<String> users = table.get(id);
		
		if(users == null) {
			
		}else {
			result = users.contains(username);
		}
		
		return result;
	}
	
	private synchronized void addInTable(Map<Integer, List<String>> table, Integer id, String username) {
		
		List<String> users = table.get(id);
		
		if(users == null) {
			List<String> temp = new ArrayList<String>();
			temp.add(username);
			table.put(id, temp);
		}else {
			if(users.contains(username)) {
				log.debug("Username {} already exist in table.", username);
			}else {
				users.add(username);
			}
		}
	}
	
	public synchronized boolean isInReadTable(Integer id, String username) {
		return isInTable(read, id, username);
	}
	
	public synchronized boolean isInWriteTable(Integer id, String username) {
		return isInTable(write, id, username);
	}
	
	public synchronized boolean isInDeleteTable(Integer id, String username) {
		return isInTable(delete, id, username);
	}
	
	public synchronized void addInReadTable(Integer id, String username) {
		addInTable(read, id, username);
	}
	
	public synchronized void addInReadTable(Integer id, User user) {
		addInReadTable(id, user.getUid());
	}
	
	public synchronized void addInWriteTable(Integer id, String username) {
		addInTable(write, id, username);
	}
	
	public synchronized void addInWriteTable(Integer id, User user) {
		addInWriteTable(id, user.getUid());
	}
	
	public synchronized void addInDeleteTable(Integer id, String username) {
		addInTable(delete, id, username);
	}
	
	public synchronized void addInDeleteTable(Integer id, User user) {
		addInDeleteTable(id, user.getUid());
	}
	
}
