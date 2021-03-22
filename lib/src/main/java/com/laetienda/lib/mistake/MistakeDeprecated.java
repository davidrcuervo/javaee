package com.laetienda.lib.mistake;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Deprecated
public class MistakeDeprecated {
	private static Logger log = LogManager.getLogger(MistakeDeprecated.class);
	
	private String status;
	private String pointer;
	private String detail;
	private String title;
	
	public MistakeDeprecated(int status, String pointer, String title, String detail) {
		setStatus(status);
		setPointer(pointer);
		setTitle(title);
		setDetail(detail);
	}
	
	public MistakeDeprecated(int status, String pointer, String detail) {
		setStatus(status);
		setPointer(pointer);
		setDetail(detail);
	}
	
	public MistakeDeprecated(String pointer, String detail) {
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
		log.debug("$ErrorCode: {}", status);
	}

	private void setPointer(String pointer) {
		this.pointer = pointer;
		log.debug("$ErrorPointer: {}", pointer);
	}

	private void setDetail(String detail) {
		this.detail = detail;
		log.debug("$ErrorDetail: {}", detail);
	}

	private void setTitle(String title) {
		this.title = title;
		log.debug("$ErrorTittle: {}", title);
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
