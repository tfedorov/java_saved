package com.tfedorov.social.twitter.tracing;

import twitter4j.Status;

public interface TweetTracingService {

  void dumpTweetToFile(Status tweet);

  void trace();

  void dumpTweetWithError(Status tweet, Exception exception);

  int getProccededTweetsCount();

  int getUsefullTweetsCount();

  void incUsefullTweetsCount();

  CurrentPerformanceBean getCurPerformance();

  boolean canProcessTrend();

}
