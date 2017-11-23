package com.tfedorov.social.twitter.processing.sentiments;

import java.util.List;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.tweet.TweetInfo;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.twitter.sentiments.SENTIMENT;

public class SentimentHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {


  public SentimentHandler(
      ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void processImpl(GeneralProcessingContext context) {
    TweetInfo tweetInfo = context.getTweetContext().getTweetInfo();
    String language = tweetInfo.getTweetTextLang();
    List<String> wordList = (List<String>) context.getTweetContext().get(TweetProcessingContext.TWEET_TERMS_WSW_LIST);
    context.getTweetContext().get(TweetProcessingContext.CLEAN_TWEET_TEXT);
    String tweetText;
    if(tweetInfo.getTweet().isRetweet()){
    	tweetText = (String) context.getTweetContext().get(TweetProcessingContext.CLEAN_RETWEETED_TEXT);
    }else{
    	tweetText = (String) context.getTweetContext().get(TweetProcessingContext.CLEAN_TWEET_TEXT);
    }
    SENTIMENT sentiment = context.getServicesContext().getSentimentStrategy().provideSentiment(wordList, tweetText, language);
    tweetInfo.setSentiment(sentiment);
  }

  @Override
  public Class getClazz() {
    return SentimentHandler.class;
  }
}
