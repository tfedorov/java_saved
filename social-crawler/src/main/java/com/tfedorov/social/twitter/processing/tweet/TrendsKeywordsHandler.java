package com.tfedorov.social.twitter.processing.tweet;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.TopicType;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

public class TrendsKeywordsHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  private static final String FAKE_KEYWORD = "FAKE_KEYWORD";

  public TrendsKeywordsHandler(ProcessingHandler<GeneralProcessingContext> successorTrue) {
    super(successorTrue);
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {

    TopicProcessingContext topicContext = context.getTopicContext();

    TopicType type = topicContext.getTopicInfo().getTopic().getType();

    TweetProcessingContext tweetContext = context.getTweetContext();
    
    String tweetLang = tweetContext.getTweetInfo().getTweetTextLang();
    
    if (type == TopicType.TRENDS) {
    	
    	//TODO: revisit
    	// skip not english words
       //if (tweetLang == null || !tweetLang.equalsIgnoreCase("en")) return;
    		
      tweetContext.getTweetInfo().markTweetAsUseful();
       
      topicContext.add(TopicProcessingContext.KEY_WORD_FOUND, FAKE_KEYWORD);
    } else {
      new IllegalArgumentException("Invalid topic type:" + topicContext.getTopicInfo().toString());
    }
  }

  @Override
  public Class<?> getClazz() {
    return TrendsKeywordsHandler.class;
  }

}
