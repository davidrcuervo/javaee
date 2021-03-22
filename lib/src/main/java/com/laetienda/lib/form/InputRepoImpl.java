package com.laetienda.lib.form;

public class InputRepoImpl implements InputRepository {

	private String name;
	private String label;
	private String id;
	private InputType type;
	private int order;
	private String placeholder;
	
	public InputRepoImpl(InputForm input) {
		name = input.name();
		label = input.label();
		id = input.id();
		type = input.type();
		order = input.order();
		placeholder = input.placeholder();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public InputType getType() {
		return type;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public String getPlaceholder() {
		return placeholder;
	}
}
