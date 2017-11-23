package com.tfedorov.social.twitter.emulation;


public interface TwitterStreamingEmulationService {

	public int sendTweets(int number);

	public String sendTweet(String jsonTweet);
}
