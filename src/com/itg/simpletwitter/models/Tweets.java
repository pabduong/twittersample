package com.itg.simpletwitter.models;

public class Tweets {

	private String id;
	private String created_at;
	private String text;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedDate() {
		return created_at;
	}

	public void setCreatedDate(String created_at) {
		this.created_at = created_at;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
