package com.laetienda.lib.form;

import java.util.List;
import java.util.Map;

public interface FormRepository {
	
	public List<InputRepository> getInputs();
	public FormMethod getMethod();
	public FormAction getAction();
	public String getButton();
	public String getName();
	public void setMethod(FormMethod method);
	public void setAction(FormAction action);
	public void setAction(String action);
	public String getClassname();
	public void setOptions(String jsonOpts);
	public void setOptions(Map<String, List<SelectOption>> options);
	public List<SelectOption> getOptions(String name);
	public String getValue(String key);
}
