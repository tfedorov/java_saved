package com.tfedorov.social.twitter.processing.intention;

import java.math.BigInteger;
import java.util.List;

import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;

import com.tfedorov.social.intention.processing.IntentionProcessingContext;
import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.ServicesContext;
import com.tfedorov.social.twitter.processing.reach.EstimatedReachProcessor;
import com.tfedorov.social.twitter.processing.tweet.TweetInfo;

/**
 * Algorithm is based on calculation average followers count per retweet
 * 
 */
public class IntentionProcessingHandler
    extends AbstractChainProcessingHandler<GeneralProcessingContext> {

  private final Logger logger = LoggerFactory.getLogger(IntentionProcessingHandler.class);

  private EstimatedReachProcessor reachProcessor;

  public IntentionProcessingHandler(ProcessingHandler<GeneralProcessingContext> successor) {
    super(successor);
    reachProcessor = new EstimatedReachProcessor();
  }

  @Override
  public Class<?> getClazz() {
    return IntentionProcessingHandler.class;
  }

  @Override
  protected void processImpl(GeneralProcessingContext context) {

    TweetProcessingContext twContext = context.getTweetContext();

    IntentionProcessingContext inContext = context.getIntentContext();

    ServicesContext svContext = context.getServicesContext();

    TopicProcessingContext topicContext = context.getTopicContext();

    TweetsAggregationDao tweetsAggregationDao = svContext.getTweetsAggregationDao();

    TweetInfo tweetInfo = twContext.getTweetInfo();

    Status status = tweetInfo.getTweet();

    // check if there is retweeted tweet
    if (status.isRetweet()) {

      Status retweetedStatus = status.getRetweetedStatus();

      long retweetedUserId = retweetedStatus.getUser().getId();

      List<BigInteger> topicIds =
          (List<BigInteger>) inContext.get(IntentionProcessingContext.USER_TRACKED_TOPICS_LIST);

      BigInteger topicId = topicContext.getTopicInfo().getTopic().getId();

      String htmlText = (String) inContext.get(IntentionProcessingContext.TWEET_HTML_MARKUP);

      if (htmlText != null && topicIds.contains(topicId)) {
        // intent was found by IntentionDectection

        logger.debug("Intention from retweeted User: [" + retweetedUserId
            + "] is tracked by topicIds:" + topicIds);

        String clearRetweetedText =
            (String) twContext.get(TweetProcessingContext.CLEAN_RETWEETED_TEXT);

        if (topicContext.get(TopicProcessingContext.KEY_WORD_FOUND) != null) {
          reachProcessor.processTweet(topicId, status, retweetedStatus, clearRetweetedText,
              tweetsAggregationDao, TopicTweetAggregate.AGGREGATE_TYPE.keyword_intention_tweets,
              htmlText);

        } else {
          reachProcessor.processTweet(topicId, status, retweetedStatus, clearRetweetedText,
              tweetsAggregationDao, TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets, htmlText);
        }

        // add word found to context
        tweetInfo.markTweetAsUseful();
      }// end of - intent was found by IntentionDectection
    }
  }
}
