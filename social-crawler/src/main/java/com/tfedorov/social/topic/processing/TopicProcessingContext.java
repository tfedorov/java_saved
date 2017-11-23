package com.tfedorov.social.topic.processing;

import com.tfedorov.social.processing.ProcessingContextImpl;


public class TopicProcessingContext extends ProcessingContextImpl {
	
	public static final String TOPIC_CONTEXT = "TOPIC_context";
	
	public static final String KEY_WORD_FOUND = "key_word_found";
	
	protected static final String TOPIC_INFO = "topic_info"; 
		
	public TopicProcessingContext(TopicInfo topicInfo) {
		super();
		add(TOPIC_INFO, topicInfo);
	}
	
	public TopicInfo getTopicInfo() {
		return (TopicInfo)get(TOPIC_INFO);
	}

	@Override
	public String getContextName() {
		return TOPIC_CONTEXT;
	}
	
	
}
