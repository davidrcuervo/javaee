package com.laetienda.lib.form;

public class SelectOptionImpl implements SelectOption {

	private String value;
	private String label;
	private boolean selected;
	private boolean disabled;
	
	public SelectOptionImpl() {
		
	}
	
	/**
	 * Example: new SelectOptionImpl(value, label, selected, disabled);
	 * @param value
	 * @param label
	 * @param selected
	 * @param disabled
	 */
	public SelectOptionImpl(String value, String label, boolean selected, boolean disabled) {
		super();
		setValue(value);
		setLabel(label);
		setSelected(selected);
		setDisabled(disabled);
	}



	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean getSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean getDisabled() {
		return disabled;
	}

	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
