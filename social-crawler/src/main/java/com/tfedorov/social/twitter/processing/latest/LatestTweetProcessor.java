package com.tfedorov.social.twitter.processing.latest;

import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.utils.date.DateUtils;
import com.tfedorov.social.word.processing.WordProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import twitter4j.User;

import java.math.BigDecimal;
import java.math.BigInteger;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Deprecated
public class LatestTweetProcessor {

  private Logger logger = LoggerFactory.getLogger(LatestTweetProcessor.class);

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public LatestTweetProcessor() {
  }

  public void processTweet(BigInteger topicId, Status status,
                           TweetsAggregationDao tweetsAggregationDao,
                           TopicTweetAggregate.AGGREGATE_TYPE type) {

    EtmPoint
        perfPoint =
        getPerformancePoint(new StringBuilder(".processTweet():").append(type).toString());

    try {
      User user = status.getUser();

      TopicTweetAggregate
          tweet =
          new TopicTweetAggregate(topicId, new BigDecimal(status.getId()).toBigInteger(),
                                  WordProcessor.removeEmotionChars(status.getText()),
                                  new BigDecimal(status.getId()).toBigInteger(),
                                  user.getScreenName(),
                                  user.getProfileImageURL(), user.getProfileImageURLHttps(),
                                  DateUtils.convertToDateTime(status.getCreatedAt()),
                                  type);

      logger.debug("Insert into " + type.name() + " tweet :" + tweet.toString());
      tweetsAggregationDao.insertAggregationTopicTweet(tweet, type);

    } finally {
      perfPoint.collect();
    }

  }

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor
        .createPoint(new StringBuilder(LatestTweetProcessor.class.toString())
                         .append(name).toString());
  }


}
