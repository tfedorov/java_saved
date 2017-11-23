package com.tfedorov.social.concurrency;

import java.util.Collection;

public interface TaskExecutionService {
	
	public void init();
	
	public void execute(Collection<Task> tasks);
	
	public void destroy();
	
	
	
}
