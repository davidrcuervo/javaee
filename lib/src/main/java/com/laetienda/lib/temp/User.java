package com.laetienda.lib.temp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.laetienda.lib.form.SelectOption;
import com.laetienda.lib.form.SelectOptionImpl;
import com.laetienda.lib.form.SelectOptionsGet;

public class User implements SelectOptionsGet  {
	
	final static private List<String> usernames = Arrays.asList("myself", "admin.brdc", "admin.sesm", "tomcat"); 

	public User() {
		
	}
	
	public List<String> getUsers(){
		return usernames;
	}

	public List<SelectOption> getSelectOptions() {
		List<SelectOption> result = new ArrayList<SelectOption>();
		
		for(String username : usernames) {
			SelectOption o = new SelectOptionImpl(username, username, false, false);
			result.add(o);
		}

		return result;
	}
}
