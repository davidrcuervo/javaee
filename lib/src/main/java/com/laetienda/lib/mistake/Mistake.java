package com.laetienda.lib.mistake;

public class Mistake {

	private int status;
	private String title;
	private String detail;
	private MistakeSource source;
	
	
	/**
	 * new Mistake(code, title, detail, pointer, parameter);</br>
	 * { </br>
     * "code":   "123", </br>
     * "source": { "pointer": "/data/attributes/firstName", "parameter": "value inserted" }, </br>
     * "title":  "Value is too short", </br>
     * "detail": "First name must contain at least three characters." </br>
     * } </br>
	 * 
	 * @param status Error code
	 * @param title 
	 * @param detail detail of the error
	 * @param pointer url path 
	 * @param parameter value of the parameter inserted
	 */
	public Mistake(int status, String title, String detail, String pointer, String parameter) {
		super();
		setStatus(status);
		setTitle(title);
		setDetail(detail);
		setSource(new MistakeSource(pointer, parameter));
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public MistakeSource getSource() {
		return source;
	}

	public void setSource(MistakeSource source) {
		this.source = source;
	}
}
