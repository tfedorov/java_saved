package com.tfedorov.social.processing;

import java.util.Map;

public interface ProcessingContext {

	public Object get(String name);
	
	public void add(String name, Object value);
	
	public void add(Map<String, Object> values);
	
	public Object remove(String name);
	
	public String getContextName();
	
	public void clear();
}
