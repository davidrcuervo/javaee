package com.laetienda.model;

import java.util.HashMap;
import java.util.List;

import com.laetienda.myapptools.Mistake;

public interface DatabaseEntity {
	
	public void addError(String list, String error);
	public void addError(int status, String pointer, String title, String detail);
	public void addError(int status, String pointer, String detail);
	public List<Mistake> getErrors();
	
	@Deprecated
	public HashMap<String, List<String>> getErrores();
	public String getName();
}
