package com.laetienda.json;

public class DbInputJsonParser {
	
	private String variable;
	private String name;
	private String type;
	
	public DbInputJsonParser() {
		
	}
	
	public DbInputJsonParser(String type, String name, String variable) {
		this.type = type;
		this.name = name;
		this.variable = variable;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
