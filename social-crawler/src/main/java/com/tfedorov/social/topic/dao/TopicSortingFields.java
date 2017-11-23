package com.tfedorov.social.topic.dao;

public enum TopicSortingFields {
	name("name"), keywords("keywords"), ctime("ctime");

	private String field;

	private TopicSortingFields(String f) {
		field = f;
	}

	public String getField() {
		return field;
	}
}
