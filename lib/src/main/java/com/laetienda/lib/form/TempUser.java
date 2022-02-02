package com.laetienda.lib.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class TempUser implements SelectOptionsGet  {
	
	final static private List<String> usernames = Arrays.asList("myself", "admin.brdc", "admin.sesm", "tomcat"); 

	public TempUser() {
		
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
