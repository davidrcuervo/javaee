package com.laetienda.frontend.repository;

import com.laetienda.lib.model.Input;

public class InputRepository {
	
	private Input input;
	
	public InputRepository(String name, String label, String type) {
		input = new Input();
		setName(name);
		setLabel(label);
		setType(type);
	}
	
	public InputRepository(
			String name, String label, String type,
			String placeholder, String glyphicon, boolean value
			) {
		input = new Input();
		setName(name);
		setLabel(label);
		setType(type);
		setPlaceholder(placeholder);
		setGlyphicon(glyphicon);
		setValue(value);
	}
	
	public Integer getId() {
		return input.getId();
	}

	public String getName() {
		return input.getName();
	}

	public void setName(String name) {
		input.setName(name);
	}

	public String getLabel() {
		return input.getLabel();
	}

	public void setLabel(String label) {
		input.setLabel(label);
	}

	public String getType() {
		return input.getType();
	}

	public void setType(String type) {
		input.setType(type);
	}

	public String getPlaceholder() {
		return input.getPlaceholder();
	}

	public void setPlaceholder(String placeholder) {
		input.setPlaceholder(placeholder);
	}

	public String getGlyphicon() {
		return input.getGlyphicon();
	}

	public void setGlyphicon(String glyphicon) {
		input.setGlyphicon(glyphicon);
	}

	public boolean isValue() {
		return input.isValue();
	}

	public void setValue(boolean value) {
		input.setValue(value);
	}

}
