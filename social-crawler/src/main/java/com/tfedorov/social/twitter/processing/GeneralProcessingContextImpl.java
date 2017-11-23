package com.tfedorov.social.twitter.processing;

import com.tfedorov.social.intention.processing.IntentionProcessingContext;
import com.tfedorov.social.processing.ProcessingContextImpl;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.word.processing.WordProcessingContext;

public class GeneralProcessingContextImpl extends ProcessingContextImpl implements GeneralProcessingContext {
	
	protected static final String GENERAL_CONTEXT = "general_context";
	
	public static final String TOPICS_INFO_LIST = "topics_info_list";
	
	public static final String LANGUAGES_SUPPORTED = "language_support_set";

	public GeneralProcessingContextImpl(TweetProcessingContext tweetContext,
                                        WordProcessingContext wordsContext, IntentionProcessingContext intentionContext,
                                        ServicesContext servicesContext) {
		//TODO: review abstraction and context composition
		add(tweetContext.getContextName(), tweetContext);
		add(wordsContext.getContextName(), wordsContext);
		add(intentionContext.getContextName(), intentionContext);
		add(servicesContext.getContextName(), servicesContext);
	}

	@Override
	public String getContextName() {
		// TODO Auto-generated method stub
		return GENERAL_CONTEXT;
	}

	@Override
	public WordProcessingContext getWordContext() {
 		return (WordProcessingContext)get(WordProcessingContext.WORDS_CONTEXT);
	}

	@Override
	public IntentionProcessingContext getIntentContext() {
		return (IntentionProcessingContext)get(IntentionProcessingContext.INTENTION_CONTEXT);
	}

	@Override
	public TweetProcessingContext getTweetContext() {
		return (TweetProcessingContext)get(TweetProcessingContext.TWEET_CONTEXT);
	}

	@Override
	public TopicProcessingContext getTopicContext() {
		return (TopicProcessingContext)get(TopicProcessingContext.TOPIC_CONTEXT);
	}

	@Override
	public ServicesContext getServicesContext() {
		return (ServicesContext)get(ServicesContext.SERVICES_CONTEXT);
	}

	@Override
	public GeneralProcessingContext copy() {
		return new GeneralProcessingContextImpl(getTweetContext(), 
				getWordContext(), getIntentContext(), getServicesContext());
	}

}
