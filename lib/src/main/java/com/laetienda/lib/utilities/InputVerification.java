package com.laetienda.lib.utilities;

import java.util.ArrayList;
import java.util.List;

public class InputVerification {
	
	
	public List<Mistake> name(String name) {
		List<Mistake> errors = new ArrayList<Mistake>();
		
		if(name == null || name.isBlank()) {
			errors.add(new Mistake("name", "name is empty"));
		}else {
			//TODO check other, alfphanumeric, max, min
		}
		
		return errors;
	}

	public static void main(String[] args) {


	}

}
