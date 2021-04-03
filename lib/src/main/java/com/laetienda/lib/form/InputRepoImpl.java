package com.laetienda.lib.form;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputRepoImpl implements InputRepository {
	final static private Logger log = LogManager.getLogger(InputRepoImpl.class);
	

	private String name;
	private String label;
	private String id;
	private InputType type;
	private int order;
	private String placeholder;
	private String options;
	
	public InputRepoImpl(InputForm input) {
		name = input.name();
		label = input.label();
		id = input.id();
		type = input.type();
		order = input.order();
		placeholder = input.placeholder();
		options = input.options();
	}
	
	@Override
	public String getOptions() {
		return options;
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
