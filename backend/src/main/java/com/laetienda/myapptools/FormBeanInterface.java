package com.laetienda.myapptools;

import java.util.HashMap;
import java.util.List;

public interface FormBeanInterface {
	
	public void addError(String list, String error);
	public void addError(int status, String pointer, String title, String detail);
	public void addError(int status, String pointer, String detail);
	public List<Mistake> getErrors(); 
	@Deprecated
	public HashMap<String, List<String>> getErrores();
	public String getJsonErrors();
}
