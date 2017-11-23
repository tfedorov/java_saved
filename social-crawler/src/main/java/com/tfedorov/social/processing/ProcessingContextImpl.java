package com.tfedorov.social.processing;

import java.util.HashMap;
import java.util.Map;

public abstract class ProcessingContextImpl implements ProcessingContext {

	private Map<String,Object> contextMap = new HashMap<String, Object>(11);
	
	@Override
	public Object get(String name) {
		return contextMap.get(name);
	}

	@Override
	public void add(String name, Object value) {
		contextMap.put(name, value);
	}

	@Override
	public void add(Map<String, Object> values) {
		contextMap.putAll(values);
	}

	@Override
	public Object remove(String name) {
		return contextMap.remove(name);
	}
	
	@Override
	public void clear() {
		contextMap.clear();
	}
}
