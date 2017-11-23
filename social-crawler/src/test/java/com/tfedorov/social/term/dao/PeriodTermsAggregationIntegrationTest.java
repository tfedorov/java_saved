package com.tfedorov.social.term.dao;

import java.math.BigInteger;
import java.util.List;

import com.tfedorov.social.topic.dao.TopicDaoImpl;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregationDaoImpl;
import com.tfedorov.social.utils.date.DateUtils;
import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate.AGGREGATE_TYPE;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDaoImpl;

public class PeriodTermsAggregationIntegrationTest {

  private static final String COMPANY_ID = "1";

  private static final BigInteger TWEET_ID = new BigInteger("2");

    private static final PeriodTermAggregate.AGGREGATE_TYPE_MAPPING TYPE_TOPIC_TERMS_P =
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p;

  private static final BigInteger TOPIC_ID = new BigInteger("777888");

  private static DriverManagerDataSource dataSource = new DriverManagerDataSource();
  private static PeriodTermAggregationDaoImpl termAggregationDao;
  private static TopicDaoImpl topicDao;
  private static TweetsAggregationDaoImpl tweetsAggregationDao;

  @BeforeClass
  public static void setUp() {

    dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
    dataSource.setUsername("dmp");
    dataSource.setPassword("dmp01");


    System.out.println("datasource initiated...");

    termAggregationDao = new PeriodTermAggregationDaoImpl();
    termAggregationDao.setDataSource(dataSource);

    topicDao = new TopicDaoImpl();
    topicDao.setDataSource(dataSource);

    tweetsAggregationDao = new TweetsAggregationDaoImpl();
    tweetsAggregationDao.setDataSource(dataSource);
  }

  @Before
  public void beforeTest() {
    termAggregationDao.cleanupAllPeriodAgregation(TOPIC_ID);
    tweetsAggregationDao.cleanupTerms(TOPIC_ID, AGGREGATE_TYPE.topic_bi_terms);
    topicDao.deleteTopicById(TOPIC_ID);
  }

  @Test
  public void testTweetsAggregationDao() {

    DateTime dateNow = DateUtils.getCurrentDateTime();
    List<PeriodTermAggregate> selected =
        termAggregationDao.getTermsByPeriod(TOPIC_ID, dateNow, 7, TYPE_TOPIC_TERMS_P, 1000);

    Assert.assertEquals(0, selected.size());

    Topic topic = new Topic();
    topic.setId(TOPIC_ID);
    topic.setCompany(COMPANY_ID);

    DateTime date = DateUtils.getCurrentDateTime();

    TopicTermAggregate.AGGREGATE_TYPE type = TopicTermAggregate.AGGREGATE_TYPE.topic_bi_terms;
    topicDao.insertTopic(topic);
    for (int i = 0; i < 900; i++) {

      TopicTermAggregate aggregate =
          new TopicTermAggregate(TOPIC_ID, "test" + i + " test" + (i + 1), date, i * 20, type);
      tweetsAggregationDao.insertAggregationTopicTerm(aggregate, TWEET_ID);
    }

    date = date.plusDays(1);

    int i =
        termAggregationDao.aggregateTermsFromRawData(TOPIC_ID, date, 1,
            PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p);

    Assert.assertTrue("Must be updated 900+1 (fake). But see " + i, i == 900 + 1);

    List list =
        termAggregationDao.getTermsByPeriod(TOPIC_ID, date, 1,
            PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_bi_terms_p, 800);

    Assert.assertTrue("List size must be more or equal to 800-1 (fake). But see " + list.size(),
        list.size() >= (800 - 1));
  }

}
