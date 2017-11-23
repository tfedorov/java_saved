package com.tfedorov.social.intention;

public enum Operator {

	INCLUDED("&"), EXCLUDED("#");

	private String operator;

	private Operator(String o) {
		operator = o;
	}

	public String getOperator() {
		return operator;
	}

}
