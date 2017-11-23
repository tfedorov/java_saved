package com.tfedorov.social.twitter.processing.latest;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.ServicesContext;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;

import twitter4j.Status;

@Deprecated
public class LatesTweetsHandler extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  private LatestTweetProcessor latestTweetProcessor;

  public LatesTweetsHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
    latestTweetProcessor = new LatestTweetProcessor();
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {
    TweetProcessingContext twContext = context.getTweetContext();
    TopicProcessingContext topicContext = context.getTopicContext();

    ServicesContext svContext = context.getServicesContext();

    TweetsAggregationDao tweetsAggregationDao = svContext.getTweetsAggregationDao();

    if (topicContext.get(TopicProcessingContext.KEY_WORD_FOUND) != null) {
      Status status = twContext.getTweetInfo().getTweet();
      Topic topic = topicContext.getTopicInfo().getTopic();

      latestTweetProcessor.processTweet(topic.getId(), status, tweetsAggregationDao,
                                        TopicTweetAggregate.AGGREGATE_TYPE.latest_tweets);
    }
  }

  @Override
  public Class getClazz() {
    return LatesTweetsHandler.class;
  }
}
