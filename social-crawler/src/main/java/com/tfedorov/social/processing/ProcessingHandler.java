package com.tfedorov.social.processing;

import org.json.JSONException;
import org.json.JSONObject;

public interface ProcessingHandler<T extends ProcessingContext> {

  void process(T context);

  Class<?> getClazz();

  JSONObject returnJson() throws JSONException;

}
