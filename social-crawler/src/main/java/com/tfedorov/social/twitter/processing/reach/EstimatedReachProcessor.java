package com.tfedorov.social.twitter.processing.reach;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import twitter4j.User;

import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.utils.date.DateUtils;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

public class EstimatedReachProcessor {

  public static final int STORE_DAYS = 30;

  public static final int MULTIPLY_FOLLOWERS = 100;

  private Logger logger = LoggerFactory.getLogger(EstimatedReachProcessor.class);

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public EstimatedReachProcessor() {}

  public void processTweet(BigInteger topicId, Status status, Status retweetedStatus,
      String clearRetweetedText, TweetsAggregationDao tweetsAggregationDao,
      TopicTweetAggregate.AGGREGATE_TYPE type, String rewriteTweetText) {

    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".processTweet():").append(type.name()).toString());
    try {

      if (retweetedStatus.getCreatedAt().before(
          DateUtils.getCurrentDateTime().minusDays(STORE_DAYS).toDate())) {
        EtmPoint perfPoint1 = getPerformancePoint(".processTweet():" + type.name() + "[OLD]");
        try {
          // don't store old retweeted status
          logger.debug("Ignore old tweet:" + retweetedStatus);
          return;
        } finally {
          perfPoint1.collect();
        }

      }


      User retweetedUser = retweetedStatus.getUser();
      long retweetedUserFollowers = retweetedUser.getFollowersCount();

      long retweetCount = retweetedStatus.getRetweetCount();
      // check retweeted count for 0
      if (retweetCount == 0) {
        retweetCount = correctRetweetCountValue(retweetedStatus);
      }

      long followersSum = extractFollowerSum(status);

      BigInteger extractTweetId = new BigDecimal(retweetedStatus.getId()).toBigInteger();

      TopicTweetAggregate tweetAggregate =
          new TopicTweetAggregate(topicId, extractTweetId, retweetCount, followersSum, type);

      int updatedCount =
          tweetsAggregationDao.updateCalcEstimadReach(tweetAggregate, type, retweetedUserFollowers);
      if (updatedCount == 0) {
        // if aggregate by topic_id, tweet_id doesn't exists
        String tweetText = clearRetweetedText;
        if (rewriteTweetText != null && !rewriteTweetText.isEmpty()) {
          tweetText = rewriteTweetText;
        }

        // This field should be setted by insert only
        tweetAggregate.setText(tweetText);
        tweetAggregate.setFromUserId(new BigDecimal(retweetedUser.getId()).toBigInteger());
        tweetAggregate.setFromUser(retweetedUser.getScreenName());
        tweetAggregate.setProfileImageUrl(retweetedUser.getProfileImageURL());
        tweetAggregate.setProfileImageUrlHttps(retweetedUser.getProfileImageURLHttps());
        tweetAggregate.setCeatedAt(DateUtils.convertToDateTime(retweetedStatus.getCreatedAt()));
        tweetAggregate.setRecentRetweets(1); // if we have at least one retweet
        tweetAggregate.setEstimatedReach(followersSum * retweetCount + retweetedUserFollowers);
        tweetAggregate.setType(type);

        logger.debug("Insert into " + type.name() + " tweet :" + tweetAggregate.toString());
        tweetsAggregationDao.insertAggregationTopicTweet(tweetAggregate, type);

      } else {
        // if aggregate by topic_id, tweet_id exists
        logger.debug("Update on " + type.name() + " tweet :" + tweetAggregate.toString());
      }


    } finally {
      perfPoint.collect();
    }


  }

  private long correctRetweetCountValue(Status retweetedStatus) {
    EtmPoint perfPoint = getPerformancePoint(".correctRetweetCountValue()");
    try {
      logger.warn("Retweet count == 0 ! Retweet id : " + retweetedStatus.getId());
      return 1;
    } finally {
      perfPoint.collect();
    }
  }

  private long extractFollowerSum(Status status) {
    User user = status.getUser();
    return user.getFollowersCount();
  }

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(EstimatedReachProcessor.class
        .toString()).append(name).toString());
  }
}
