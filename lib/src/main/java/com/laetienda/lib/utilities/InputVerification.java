package com.laetienda.lib.utilities;

import java.util.ArrayList;
import java.util.List;

import com.laetienda.lib.mistake.MistakeDeprecated;

@Deprecated
public class InputVerification {
	
	
	public List<MistakeDeprecated> name(String name) {
		List<MistakeDeprecated> errors = new ArrayList<MistakeDeprecated>();
		
		if(name == null || name.isBlank()) {
			errors.add(new MistakeDeprecated("name", "name is empty"));
		}else {
			//TODO check other, alfphanumeric, max, min
		}
		
		return errors;
	}

	public static void main(String[] args) {


	}

}
