package com.tfedorov.social.twitter.processing;

import com.tfedorov.social.intention.processing.IntentionProcessingContext;
import com.tfedorov.social.processing.ProcessingContext;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.word.processing.WordProcessingContext;

public interface GeneralProcessingContext extends ProcessingContext {
	
	public WordProcessingContext getWordContext();
	
	public IntentionProcessingContext getIntentContext();
	
	public TweetProcessingContext getTweetContext();
	
	public TopicProcessingContext getTopicContext();
	
	public ServicesContext getServicesContext();
	
	public GeneralProcessingContext copy();

}
