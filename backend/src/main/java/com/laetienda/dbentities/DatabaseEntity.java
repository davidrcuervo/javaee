package com.laetienda.dbentities;

import java.util.HashMap;
import java.util.List;

public interface DatabaseEntity {
	public HashMap<String, List<String>> getErrors();
	public void addError(String list, String error);
	public String getName();
}
