package com.tfedorov.social.twitter.processing.filtering;

import java.util.Set;

import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import twitter4j.Status;

import com.tfedorov.social.processing.AbstractConditionalProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.word.processing.WordProcessingContext;
import com.tfedorov.social.word.processing.WordProcessor;

public class BlackListFilterHandler
    extends AbstractConditionalProcessingHandler<GeneralProcessingContext> {

  public BlackListFilterHandler(ProcessingHandler<GeneralProcessingContext> successorTrue,
      ProcessingHandler<GeneralProcessingContext> successorFalse) {
    super(successorTrue, successorFalse);

  }

  @Override
  protected boolean processImpl(GeneralProcessingContext context) {

    TweetProcessingContext twContext = context.getTweetContext();
    WordProcessingContext wdContext = context.getWordContext();

    Status tweet = twContext.getTweetInfo().getTweet();
    String tweetText;
    if (!tweet.isRetweet()) {
      tweetText = tweet.getText();
    } else {
      tweetText = tweet.getRetweetedStatus().getText();
    }
    Set<String> blackWords = wdContext.getWordsInfo().getBlackWords();

    String resultStr = WordProcessor.hasTextWordFromSet(tweetText, blackWords);

    boolean result = false;

    if (resultStr != null) {
      twContext.add(TweetProcessingContext.BLACK_WORD_FOUND, resultStr);
      result = true;
    }

    return result;

  }

  @Override
  public Class<?> getClazz() {
    return BlackListFilterHandler.class;
  }

}
