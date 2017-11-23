package com.tfedorov.social.twitter.streaming.performance;

public interface TwitterStreamingHealthService {
	
	public void check();

	public int getFreeMemoryInPercentage();
	
	public long getUsedMemoryInKB();

	public boolean isActive();
	
	public void stop();
	
	public void start();
}
