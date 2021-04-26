package com.laetienda.lib.http;

import java.util.List;

import com.laetienda.lib.mistake.Mistake;

public class HttpClientException extends Exception {
	private static final long serialVersionUID = 1L;

	private Integer code;
	private List<Mistake> mistakes;

	public HttpClientException() {
		
	}

	public HttpClientException(String message) {
		super(message);
	}

	public HttpClientException(Throwable cause) {
		super(cause);
	}

	public HttpClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public HttpClientException(String message, Integer code, List<Mistake> errors) {
		super(message);
		setCode(code);
		setMistakes(errors);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<Mistake> getMistakes() {
		return mistakes;
	}

	public void setMistakes(List<Mistake> mistakes) {
		this.mistakes = mistakes;
	}
}
