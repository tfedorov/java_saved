package com.tfedorov.social.intention.processing;

import com.tfedorov.social.processing.ProcessingContextImpl;

public class IntentionProcessingContext extends ProcessingContextImpl {

	public static final String INTENTION_CONTEXT = "intention_context";
	
	public static final String USER_TRACKED_TOPICS_LIST = "user_topics_list";
	
	public static final String TWEET_HTML_MARKUP = "tweet_html_markup";
	
	@Override
	public String getContextName() {
		return INTENTION_CONTEXT;
	}

}
