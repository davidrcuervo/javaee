package com.latienda.model.utils;

public class Validate {

	public Validate() {
		
	}
	
	public boolean isEmpty(String input) {
		boolean result = true;
		
		if(input == null || input.isBlank() || input.isEmpty()) {
			result = true;
		}else {
			result = false;
		}
		
		return result;
	}

}
