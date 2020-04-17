package com.laetienda.myapptools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mistake {
	private static Logger log = LogManager.getLogger(Mistake.class);
	
	private String status;
	private String pointer;
	private String detail;
	private String title;
	
	public Mistake(int status, String pointer, String title, String detail) {
		setStatus(status);
		setPointer(pointer);
		setTitle(title);
		setDetail(detail);
	}
	
	public Mistake(int status, String pointer, String detail) {
		setStatus(status);
		setPointer(pointer);
		setDetail(detail);
	}
	
	public Mistake(String pointer, String detail) {
		setPointer(pointer);
		setDetail(detail);
	}
	
	public String getJson() {
		String result = "{";
		if(status != null && !status.isBlank())	result += String.format("\"status\": \"%s\",", status);
		if(pointer != null && !pointer.isBlank())	result += String.format("\"source\": { \"pointer\": \"%s\"},", pointer);
		if(title != null && !title.isBlank())	result += String.format("\"title\": \"%s\",", title);
		if(detail != null && !detail.isBlank())	result += String.format("\"detail\": \"%s\"", detail);
		result += "}";
		
//		log.debug("Errores: {}", result);
		
		
		return result;
	}
	
	private void setStatus(int status) {
		this.status = Integer.toString(status);
	}

	private void setPointer(String pointer) {
		this.pointer = pointer;
	}

	private void setDetail(String detail) {
		this.detail = detail;
	}

	private void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public String getPointer() {
		return pointer;
	}

	public String getDetail() {
		return detail;
	}

	public String getTitle() {
		return title;
	}
}
