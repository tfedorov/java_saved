package com.tfedorov.social.cleaner;

import java.math.BigInteger;
import java.util.List;

import com.tfedorov.social.topic.dao.TopicDao;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.utils.date.DateUtils;
import org.joda.time.DateMidnight;
import org.joda.time.base.BaseDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tfedorov.social.topic.Topic;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Deprecated
@Service(value = "latestTweetsCleaner")
public class LatestTweetsCleaner {

  public static final int THREAD_SLEEP_TIME = 100;
  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private static final Logger LOGGER = LoggerFactory.getLogger(LatestTweetsCleaner.class);

  @Autowired
  @Qualifier(value = "tweetsAggregationDao")
  private TweetsAggregationDao tweetsAggregationDao;

  @Autowired
  @Qualifier(value = "topicDao")
  private TopicDao topicDao;

  @Value(value = "${agg.social.latest.cleaner.limit:1000}")
  private int limit;


  public void cleanLatestTweets() {
    EtmPoint perfPoint = getPerformancePoint(".cleanLatestTweets()");
    try {
      LOGGER.info(
          "Starting working cleaner for latest tweets those gathered from yesterday midnight");
      DateMidnight currentMidnight = DateUtils.getCurrentMidnight();
      cleanByDate(currentMidnight);
      LOGGER
          .info("Finished working cleaner for latest tweets that gathered from yesterday midnight");
    } finally {
      perfPoint.collect();
    }
  }

  private void cleanLatestTweetsByTopic(Topic topic, BaseDateTime dateTime) {
    EtmPoint perfPoint = getPerformancePoint(".cleanLatestTweetsByTopic()");
    try {
      while (tweetsAggregationDao.cleanLatestTweets(topic.getId(), limit, dateTime) != 0) {
        Thread.sleep(THREAD_SLEEP_TIME);
      }
    } catch (InterruptedException e) {
      LOGGER.error("Error with thread sleeping", e);
    } finally {
      perfPoint.collect();
    }
  }

  public void cleanByTopicAndDate(BigInteger topicId, BaseDateTime baseDateTime) {
    EtmPoint perfPoint = getPerformancePoint(".getCleanerDates()");
    try {
      while (tweetsAggregationDao.cleanLatestTweets(topicId, limit, baseDateTime) != 0) {
        Thread.sleep(THREAD_SLEEP_TIME);
      }
    } catch (InterruptedException e) {
      LOGGER.error("error with thread sleeping", e);
    } finally {
      perfPoint.collect();
    }
  }

  public void cleanByDate(BaseDateTime baseDateTime) {
    LOGGER.info("Starting latest tweets cleaner");
    EtmPoint perfPoint = getPerformancePoint(".cleanByDate()");
    try {
      List<Topic> topicList = topicDao.getTrackedTopics();
      for (Topic aTopicList : topicList) {
        cleanLatestTweetsByTopic(aTopicList, baseDateTime);
      }
      LOGGER.info("Finished cleaning of latest tweets");
    } finally {
      perfPoint.collect();
    }
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(LatestTweetsCleaner.class
                                                                .toString()).append(name)
                                              .toString());
  }

}
