package com.laetienda.model.webdb;

import java.io.Serializable;

public class ThankyouPage implements Serializable {
	private static final long serialVersionUID = 1L;

	private String key;
	private String source;
	private String title;
	private String Description;
	private String actionLink;
	private String actionText;
	
	public ThankyouPage() {

	}
	
	public ThankyouPage(String title, String description) {
		setTitle(title);
		setDescription(description);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getActionLink() {
		return actionLink;
	}

	public void setActionLink(String actionLink) {
		this.actionLink = actionLink;
	}

	public String getActionText() {
		return actionText;
	}

	public void setActionText(String actionText) {
		this.actionText = actionText;
	}
}
