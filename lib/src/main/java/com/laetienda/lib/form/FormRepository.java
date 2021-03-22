package com.laetienda.lib.form;

import java.util.List;

public interface FormRepository {
	
	public List<InputRepository> getInputs();
	public FormMethod getMethod();
	public String getAction();
	public String getButton();
	public void setMethod(FormMethod method);
}
