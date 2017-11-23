package com.tfedorov.social.web.exception;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectExceptionMessage {

	private final JSONObject errMesssage = new JSONObject();

	/**
	 * @return the errMesssage
	 */
	public JSONObject getErrMesssage() {
		return errMesssage;
	}

	public JsonObjectExceptionMessage(Exception e) {
		JSONObject err = new JSONObject();
		try {
			errMesssage.put("status", "502").put("message", e.getMessage());
			err.put("errors", errMesssage);
		} catch (JSONException e1) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return errMesssage.toString();
	}

}
