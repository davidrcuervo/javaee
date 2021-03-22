package com.laetienda.lib.mistake;

public class MistakeSource {

	private String pointer;
	private String parameter;
	
	public MistakeSource() {
		
	}
	
	public MistakeSource(String pointer, String parameter) {
		setPointer(pointer);
		setParameter(parameter);
	}

	public String getPointer() {
		return pointer;
	}

	public void setPointer(String poiter) {
		this.pointer = poiter;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
