package com.laetienda.backend.repository;

import java.util.HashMap;
import java.util.List;

import com.laetienda.lib.mistake.MistakeDeprecated;
import com.laetienda.lib.model.Objeto;

public interface RepositoryInterface {
	
	public void setObjeto(Objeto objeto);
	public void addError(String list, String error);
	public void addError(int status, String pointer, String title, String detail);
	public void addError(int status, String pointer, String detail);
	public List<MistakeDeprecated> getErrors();
	public Objeto getObjeto();
	
	@Deprecated
	public HashMap<String, List<String>> getErrores();
	public String getName();
}
