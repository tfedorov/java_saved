package com.tfedorov.social.term.dao;

import java.math.BigInteger;

import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDaoImpl;
import com.tfedorov.social.utils.date.DateUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class TopicTermAggregationIntegrationTest {

    private static final BigInteger TWEET_ID = new BigInteger("2");

    @Test
  public void testInsertTopicTerms() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
    dataSource.setUsername("dmp");
    dataSource.setPassword("dmp01");

    System.out.println("datasource initiated...");

    TweetsAggregationDaoImpl dao = new TweetsAggregationDaoImpl();

    dao.setDataSource(dataSource);

    DateTime date = DateUtils.getCurrentDateTime();

    TopicTermAggregate topicTerm =
        new TopicTermAggregate(new BigInteger("11234"),
            "c2c4c6c8c11c14c17c20c23c26c29c32c35c38c41c44", date, 0,
            TopicTermAggregate.AGGREGATE_TYPE.topic_terms);
    // c47c50c53
    dao.insertAggregationTopicTerm(topicTerm, TWEET_ID);
  }
}
