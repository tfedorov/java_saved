package com.tfedorov.social.twitter.streaming;

public interface TwitterStreamingService {

	public void init();

	public void startCrawler();
	
	public void stopCrawler();

	public void shutDownCrawler();

	public void reloadWords();

	public void reloadTopics();

	public void updateTopics();
	
	public boolean isActive();
	
	public String getRunInfo();
	
	public int getTopicsNumber();
	
	public int getTweetsQueueSize();

}
