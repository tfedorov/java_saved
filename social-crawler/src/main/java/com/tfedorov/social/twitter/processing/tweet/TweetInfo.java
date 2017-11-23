package com.tfedorov.social.twitter.processing.tweet;

import com.tfedorov.social.twitter.sentiments.SENTIMENT;

import twitter4j.Status;

public class TweetInfo {

  private Status tweet;

  private SENTIMENT sentiment;
  
  private volatile boolean useful = false;
  
  private String tweetTextLang;
  

  public TweetInfo(Status tweet) {
    super();
    this.tweet = tweet;
  }

  public Status getTweet() {
    return tweet;
  }

  public String getTweetTextLang() {
	  return tweetTextLang;
  }
  /**
   * @param tweetTextLang the tweetTextLang to set
   */
  public void setTweetTextLang(String tweetTextLang) {
  	this.tweetTextLang = tweetTextLang;
  }

  public void setSentiment(SENTIMENT sentiment) {
    this.sentiment = sentiment;
  }

  public SENTIMENT getSentiment() {
    return sentiment;
  }

	public boolean isUseful() {
		return useful;
	}

	public void markTweetAsUseful() {
		useful = true;
	}

  @Override
  public String toString() {
    return new StringBuilder("TWEET_INFO{").append(tweet).append("}").toString();
  }



}
