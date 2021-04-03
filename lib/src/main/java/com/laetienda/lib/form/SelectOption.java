package com.laetienda.lib.form;

public interface SelectOption {
	
	public String getValue();
	public String getLabel();
	public boolean getSelected();
	public boolean getDisabled();
	public void setSelected(boolean flag);
	public void setDisabled(boolean flag);
	
}
