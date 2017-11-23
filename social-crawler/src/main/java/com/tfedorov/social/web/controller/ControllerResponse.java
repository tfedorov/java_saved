package com.tfedorov.social.web.controller;

public final class ControllerResponse {

	private ControllerResponse() {
		throw new UnsupportedOperationException();
	}

	public static final String EMPTY_JSON = "{}";

	public static final String STATUS_SUCCESS = "{\"status\": \"success\"}";

	public static final String STATUS_INTERNAL_SERVER_ERROR = "{\"errors\": {\"message\": \"Internal server error %1$s\",\"code\": 500}}";

	public static final String STATUS_ERROR = "{\"errors\": {\"message\": \"%1$s\",\"code\": %2$d}}";
}
