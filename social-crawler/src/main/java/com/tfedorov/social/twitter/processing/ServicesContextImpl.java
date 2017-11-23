package com.tfedorov.social.twitter.processing;

import com.tfedorov.social.concurrency.TaskExecutionService;
import com.tfedorov.social.intention.IntentionService;
import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.processing.ProcessingContextImpl;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.sentiments.strategy.SentimentStrategy;
import com.tfedorov.social.twitter.tracing.TweetTracingService;

public class ServicesContextImpl extends ProcessingContextImpl implements ServicesContext {


  public ServicesContextImpl(TweetsAggregationDao tweetsAggregationDao,
      IntentionService intentionService, TweetTracingService tweetTracingService,
      TaskExecutionService taskExecutionService, SentimentStrategy sentimentStrategy,
      StemmingService stemmingService) {
    super();
    add(TWEETS_AGGREGATION_DAO, tweetsAggregationDao);
    add(INTENTION_SERVICE, intentionService);
    add(TRACE_SERVICE, tweetTracingService);
    add(TASK_EXECUTION_SERVICE, taskExecutionService);
    add(SENTIMENT_STRATEGY, sentimentStrategy);
    add(STEMMING_SERVICE, stemmingService);
  }


  @Override
  public TweetTracingService getTweetTracingService() {
    return (TweetTracingService) get(TRACE_SERVICE);
  }


  @Override
  public TweetsAggregationDao getTweetsAggregationDao() {
    return (TweetsAggregationDao) get(TWEETS_AGGREGATION_DAO);
  }

  @Override
  public IntentionService getIntentionService() {
    return (IntentionService) get(INTENTION_SERVICE);
  }

  @Override
  public TaskExecutionService getTaskExecutionService() {
    return (TaskExecutionService) get(TASK_EXECUTION_SERVICE);
  }


  @Override
  public String getContextName() {
    return SERVICES_CONTEXT;
  }

  @Override
  public SentimentStrategy getSentimentStrategy() {
    return (SentimentStrategy) get(SENTIMENT_STRATEGY);
  }


  @Override
  public StemmingService getStemmingService() {
    return (StemmingService) get(STEMMING_SERVICE);
  }
}
