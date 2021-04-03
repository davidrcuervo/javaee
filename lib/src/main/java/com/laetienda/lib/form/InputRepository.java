package com.laetienda.lib.form;

public interface InputRepository {
	
	public String getName();
	public String getLabel();
	public String getId();
	public InputType getType();
	public int getOrder();
	public String getPlaceholder();
	public String getOptions();
}
