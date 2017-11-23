package com.tfedorov.social.cleaner;

import java.util.List;

import javax.annotation.PostConstruct;

import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregationDao;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.utils.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate.AGGREGATE_TYPE;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("socialCleanerService")
public class SocialCleanerServiceImpl implements SocialCleanerService {

  private static final Logger logger = LoggerFactory.getLogger(SocialCleanerServiceImpl.class);

  private final int period;

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private final TweetsAggregationDao tweetsAggregationDao;

  private PeriodTermAggregationDao termsAggregationDao;

  private final int popularPeriod;
  private final int popularRetweetsCount;

  private static final int MAX_TWEET_COUNT = 1000;

  @Autowired
  public SocialCleanerServiceImpl(@Value("${agg.social.cleaner.period:30}") int period,
      @Value("${agg.social.cleaner.popular.period:7}") int popularPeriod,
      @Value("${agg.social.cleaner.popular.retweets.count:1}") int popularRetweetsCount,
      TweetsAggregationDao tweetsAggregationDao, PeriodTermAggregationDao termsAggregationDao) {
    super();
    this.period = period;
    this.popularPeriod = popularPeriod;
    this.popularRetweetsCount = popularRetweetsCount;
    this.tweetsAggregationDao = tweetsAggregationDao;
    this.termsAggregationDao = termsAggregationDao;
  }

  @Override
  public void cleanTweetStatistics() {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(SocialCleanerServiceImpl.class + ".cleanTweetStatistics()");
    try {
      logger.debug(" ---------------  CLEANER START RUN with period = " + period
          + " days ----------------");
      clearTweetsAggregationStatisticByDates();
      clearTermsAggregationStatisticByDates();
      clearPopularTweetStatisticByDates();
      clearPopularTweetStatisticByLimit();
      clearIntentionTweetStatisticByLimit();
    } finally {
      perfPoint.collect();
    }
  }

  private void clearTweetsAggregationStatisticByDates() {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(SocialCleanerServiceImpl.class
            + ".clearTweetsAggregationStatisticByDates()");
    try {
      int count =
          tweetsAggregationDao.cleanupAgregationByDates(DateUtils.getMidnightDaysOr30Ago(period));
      logger.info("Cleaned " + count + " rows from aggregation tables");
    } finally {
      perfPoint.collect();
    }
  }

  private void clearTermsAggregationStatisticByDates() {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(SocialCleanerServiceImpl.class
            + ".clearTermsAggregationStatisticByDates()");
    try {
      int count =
          termsAggregationDao.cleanupAgregationByDates(DateUtils.getMidnightDaysOr30Ago(period));
      logger.info("Cleaned " + count + " rows from preaggregation tables");
    } finally {
      perfPoint.collect();
    }
  }


  private void clearPopularTweetStatisticByDates() {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(SocialCleanerServiceImpl.class
            + ".clearPopularTweetStatisticByDates()");
    try {
      int count =
          tweetsAggregationDao.cleanupPopularByRetweetsAndDates(popularRetweetsCount,
              DateUtils.getMidnightDaysOr30Ago(popularPeriod), AGGREGATE_TYPE.popular_tweets);
      logger.info("Cleaned " + count + " rows from popular tweets tables");
    } finally {
      perfPoint.collect();
    }
  }

  private void clearPopularTweetStatisticByLimit() {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(SocialCleanerServiceImpl.class
            + ".clearPopularTweetStatistic()");
    try {
      int count = 0;
      // load all topic id's where popular tweet counts bigger than MAX_POPULAR_COUNT
      List<Integer> topicIds =
          tweetsAggregationDao.loadTopicIdsWithCountsMoreThan(MAX_TWEET_COUNT,
              TweetsAggregationDao.TABLE_NAME.popular_tweets);
      // remove popular tweet where count by topic more than MAX_POPULAR_COUNT
      for (Integer topicId : topicIds) {
        count +=
            tweetsAggregationDao.deleteTweetByMaxCount(topicId, MAX_TWEET_COUNT,
                TweetsAggregationDao.TABLE_NAME.popular_tweets);
      }
      logger.info("Deleted " + count + " popular tweet where count by topic more than "
          + MAX_TWEET_COUNT);
    } finally {
      perfPoint.collect();
    }

  }

  private void clearIntentionTweetStatisticByLimit() {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(SocialCleanerServiceImpl.class
            + ".clearIntentionTweetStatisticByLimit()");
    try {
      int count = 0;
      // load all topic id's where intention tweet counts bigger than MAX_TWEET_COUNT
      List<Integer> topicIds =
          tweetsAggregationDao.loadTopicIdsWithCountsMoreThan(MAX_TWEET_COUNT,
              TweetsAggregationDao.TABLE_NAME.intention_tweets);
      // remove intention tweet where count by topic more than MAX_TWEET_COUNT
      for (Integer topicId : topicIds) {
        count +=
            tweetsAggregationDao.deleteTweetByMaxCount(topicId, MAX_TWEET_COUNT,
                TweetsAggregationDao.TABLE_NAME.intention_tweets);
      }
      logger.info("Deleted " + count + " intention tweet where count by topic more than "
          + MAX_TWEET_COUNT);
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  @PostConstruct
  public void init() {
    // TODO Auto-generated method stub

  }

}
