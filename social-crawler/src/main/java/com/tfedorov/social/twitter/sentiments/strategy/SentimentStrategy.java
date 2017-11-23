package com.tfedorov.social.twitter.sentiments.strategy;

import com.tfedorov.social.twitter.sentiments.SENTIMENT;

import java.util.List;

public interface SentimentStrategy {
  
  SENTIMENT provideSentiment(List<String> wordsList,String tweetText, String lang);

  void reloadWords();

}
