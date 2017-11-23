package com.tfedorov.social.twitter.processing.retweet;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.processing.ServicesContext;
import com.tfedorov.social.twitter.processing.reach.EstimatedReachProcessor;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import twitter4j.Status;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;


/**
/**
 * Algorithm is based on calculation average followers count per retweet
 *
 */

public class TopRetweetsHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {
	
	public static final int MULTIPLY_FOLLOWERS = 100;
	
	private EstimatedReachProcessor reachProcessor;
	
	public TopRetweetsHandler(ProcessingHandler<GeneralProcessingContext> successor) {
		super(successor);
		reachProcessor = new EstimatedReachProcessor();
	}

	@Override
	public Class<?> getClazz() {
		return TopRetweetsHandler.class;
	}

	@Override
	protected void processImpl(GeneralProcessingContext context) {
		TweetProcessingContext twContext = context.getTweetContext();
		TopicProcessingContext topicContext = context.getTopicContext();
		
		ServicesContext svContext = context.getServicesContext();
		
		TweetsAggregationDao tweetsAggregationDao = svContext.getTweetsAggregationDao();
		
		Status status = twContext.getTweetInfo().getTweet();
	
		//check context if there topic keyword was found in retweeted text and we are interesting it tweets with retweets > 0
		if (status.isRetweet() && topicContext.get(TopicProcessingContext.KEY_WORD_FOUND) != null) {
			
			Topic topic = topicContext.getTopicInfo().getTopic();
			
			Status retweetedStatus = status.getRetweetedStatus();
			
			String clearRetweetedText = (String)twContext.get(TweetProcessingContext.CLEAN_RETWEETED_TEXT);
			
			reachProcessor.processTweet(topic.getId(), status, retweetedStatus, clearRetweetedText, tweetsAggregationDao, 
					TopicTweetAggregate.AGGREGATE_TYPE.popular_tweets, null);
		
		}
	}

}
