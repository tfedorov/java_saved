package com.tfedorov.social.twitter.processing;

import com.tfedorov.social.concurrency.TaskExecutionService;
import com.tfedorov.social.intention.IntentionService;
import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.processing.ProcessingContext;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.sentiments.strategy.SentimentStrategy;
import com.tfedorov.social.twitter.tracing.TweetTracingService;

public interface ServicesContext extends ProcessingContext {

  public static final String SERVICES_CONTEXT = "services_context";

  public static final String TWEETS_AGGREGATION_DAO = "tweets_aggregation_dao";

  public static final String INTENTION_SERVICE = "intention_service";

  public static final String TRACE_SERVICE = "trace_service";

  public static final String TASK_EXECUTION_SERVICE = "task_execution_service";

  public static final String STEMMING_SERVICE = "stemming_service";

  String SENTIMENT_STRATEGY = "sentiment_strategy";

  public TweetsAggregationDao getTweetsAggregationDao();

  public IntentionService getIntentionService();

  public TweetTracingService getTweetTracingService();

  public TaskExecutionService getTaskExecutionService();

  SentimentStrategy getSentimentStrategy();

  public StemmingService getStemmingService();

}
