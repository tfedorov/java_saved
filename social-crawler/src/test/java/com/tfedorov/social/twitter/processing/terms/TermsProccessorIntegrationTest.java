/**
 * 
 */
package com.tfedorov.social.twitter.processing.terms;

import java.math.BigInteger;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import twitter4j.Status;
import twitter4j.TwitterException;

import com.tfedorov.social.concurrency.TaskExecutionServiceImpl;
import com.tfedorov.social.testutil.StatusBuilder;
import com.tfedorov.social.testutil.TopicBuilder;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDaoImpl;
import com.tfedorov.social.utils.date.DateUtils;

/**
 * @author tfedorov
 * 
 */
public class TermsProccessorIntegrationTest {

  private static final String STANDART_TERM = "example";
  private static final String CHAR52_TERM = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
  private static final String TRIMMED_TERM = CHAR52_TERM.substring(0, 50);

  private static TermsProccessor proccessor = new TermsProccessor();
  private static TweetsAggregationDaoImpl dao;
  private Status standartStatus = StatusBuilder.buildStandart();
  private DateMidnight standartStatusCreated = DateUtils.convertToDateMidnight(standartStatus
      .getCreatedAt());

  @BeforeClass
  public static void setup() {
    dao = new TweetsAggregationDaoImpl();
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
    dataSource.setUsername("dmp");
    dataSource.setPassword("dmp01");
    dao.setDataSource(dataSource);

  }

  @Before
  public void before() {
    dao.cleanupTerms(TopicBuilder.STANDART_TOPIC_ID, TopicTermAggregate.AGGREGATE_TYPE.topic_terms,
        STANDART_TERM);

  }

  @Test
  public void testProccessTerm() throws TwitterException {

    TaskExecutionServiceImpl service = new TaskExecutionServiceImpl(1);

    TopicTermAggregate selectedFromDb = selectStandartTerm();
    Assert.assertNull(selectedFromDb);
    // test insert
    proccessor.proccessTerm(standartStatus, dao, TopicBuilder.buildStandart(), STANDART_TERM,
        TopicTermAggregate.AGGREGATE_TYPE.topic_terms, service);

    selectedFromDb = selectStandartTerm();
    Assert.assertNotNull(selectedFromDb);
    Assert.assertEquals(TopicBuilder.STANDART_TOPIC_ID, selectedFromDb.getTopicId());
    Assert.assertEquals(STANDART_TERM, selectedFromDb.getTerm());
    Assert.assertEquals(1, selectedFromDb.getTermsCount());
    Assert.assertEquals(standartStatusCreated, selectedFromDb.getDate());
    Assert.assertEquals(StatusBuilder.STANDART_TWEET_ID, selectedFromDb.getTw1Id().toString());
    Assert.assertNull(selectedFromDb.getTw2Id());
    Assert.assertNull(selectedFromDb.getTw3Id());

    // test update
    String twetTextUpdated1 = STANDART_TERM + " updated1";
    String updatedTweetId = "100000000000000000";
    Status update1 = StatusBuilder.buildStandartWithText(updatedTweetId, twetTextUpdated1);

    proccessor.proccessTerm(update1, dao, TopicBuilder.buildStandart(), STANDART_TERM,
        TopicTermAggregate.AGGREGATE_TYPE.topic_terms, service);
    selectedFromDb =
        selectTerm(STANDART_TERM, DateUtils.convertToDateMidnight(update1.getCreatedAt()));
    Assert.assertNotNull(selectedFromDb);
    Assert.assertEquals(2, selectedFromDb.getTermsCount());
    Assert.assertEquals(TopicBuilder.STANDART_TOPIC_ID, selectedFromDb.getTopicId());
    Assert.assertEquals(STANDART_TERM, selectedFromDb.getTerm());
    Assert.assertEquals(updatedTweetId, selectedFromDb.getTw1Id().toString());
    Assert.assertEquals(StatusBuilder.STANDART_TWEET_ID, selectedFromDb.getTw2Id().toString());
    Assert.assertNull(selectedFromDb.getTw3Id());
    /*
     * //update same tweet proccessor.proccessTerm(update1, dao, TopicBuilder.buildStandart() ,
     * STANDART_TERM , TopicTermAggregate.AGGREGATE_TYPE.topic_terms, service); selectedFromDb =
     * selectTerm(STANDART_TERM, DateUtils.convertToDateMidnight(update1.getCreatedAt()));
     * Assert.assertNotNull(selectedFromDb); Assert.assertEquals(3, selectedFromDb.getTermsCount());
     * Assert.assertEquals(TopicBuilder.STANDART_TOPIC_ID, selectedFromDb.getTopicId());
     * Assert.assertEquals(STANDART_TERM, selectedFromDb.getTerm());
     * Assert.assertEquals(updatedTweetId, selectedFromDb.getTw1_id().toString());
     * Assert.assertEquals(StatusBuilder.STANDART_TWEET_ID, selectedFromDb.getTw2_id().toString());
     * Assert.assertNull(selectedFromDb.getTw3_id());
     */

    // test 2nd update
    String twetTextUpdated2 = STANDART_TERM + " updated2";
    String updated2TweetId = "200000000000000000";
    Status update2 = StatusBuilder.buildStandartWithText(updated2TweetId, twetTextUpdated2);

    proccessor.proccessTerm(update2, dao, TopicBuilder.buildStandart(), STANDART_TERM,
        TopicTermAggregate.AGGREGATE_TYPE.topic_terms, service);
    selectedFromDb =
        selectTerm(STANDART_TERM, DateUtils.convertToDateMidnight(update2.getCreatedAt()));
    Assert.assertNotNull(selectedFromDb);
    // Assert.assertEquals(4, selectedFromDb.getTermsCount());
    Assert.assertEquals(3, selectedFromDb.getTermsCount());
    Assert.assertEquals(TopicBuilder.STANDART_TOPIC_ID, selectedFromDb.getTopicId());
    Assert.assertEquals(STANDART_TERM, selectedFromDb.getTerm());
    Assert.assertEquals(updated2TweetId, selectedFromDb.getTw1Id().toString());
    Assert.assertEquals(updatedTweetId, selectedFromDb.getTw2Id().toString());
    Assert.assertEquals(StatusBuilder.STANDART_TWEET_ID, selectedFromDb.getTw3Id().toString());


  }

  @Test
  public void testProccessTermTrim() throws TwitterException {
    TaskExecutionServiceImpl service = new TaskExecutionServiceImpl(1);

    TopicTermAggregate selectedFromDb = selectTerm(TRIMMED_TERM, standartStatusCreated);
    Assert.assertNull(selectedFromDb);
    // test insert

    proccessor.proccessTerm(standartStatus, dao, TopicBuilder.buildStandart(), CHAR52_TERM,
        TopicTermAggregate.AGGREGATE_TYPE.topic_terms, service);
    selectedFromDb = selectTerm(TRIMMED_TERM, standartStatusCreated);
    Assert.assertNotNull(selectedFromDb);
    Assert.assertEquals(TopicBuilder.STANDART_TOPIC_ID, selectedFromDb.getTopicId());
    Assert.assertEquals(TRIMMED_TERM, selectedFromDb.getTerm());
    Assert.assertEquals(1, selectedFromDb.getTermsCount());
    Assert.assertEquals(standartStatusCreated, selectedFromDb.getDate());
    Assert.assertEquals(StatusBuilder.STANDART_TWEET_ID, selectedFromDb.getTw1Id().toString());
    Assert.assertNull(selectedFromDb.getTw2Id());
    Assert.assertNull(selectedFromDb.getTw3Id());

    // test update
    String twetTextUpdated1 = CHAR52_TERM + " updated1";
    String updatedTweetId = "100000000000000000";
    Status update1 = StatusBuilder.buildStandartWithText(updatedTweetId, twetTextUpdated1);

    proccessor.proccessTerm(update1, dao, TopicBuilder.buildStandart(), CHAR52_TERM,
        TopicTermAggregate.AGGREGATE_TYPE.topic_terms, service);
    selectedFromDb =
        selectTerm(TRIMMED_TERM, DateUtils.convertToDateMidnight(update1.getCreatedAt()));
    Assert.assertNotNull(selectedFromDb);
    Assert.assertEquals(2, selectedFromDb.getTermsCount());

    // test 2nd update
    String twetTextUpdated2 = CHAR52_TERM + " updated2";
    String updated2TweetId = "200000000000000000";
    Status update2 = StatusBuilder.buildStandartWithText(updated2TweetId, twetTextUpdated2);

    proccessor.proccessTerm(update2, dao, TopicBuilder.buildStandart(), CHAR52_TERM,
        TopicTermAggregate.AGGREGATE_TYPE.topic_terms, service);
    selectedFromDb =
        selectTerm(TRIMMED_TERM, DateUtils.convertToDateMidnight(update2.getCreatedAt()));
    Assert.assertNotNull(selectedFromDb);
    Assert.assertEquals(3, selectedFromDb.getTermsCount());
  }

  private TopicTermAggregate selectStandartTerm() {
    return selectTerm(STANDART_TERM, standartStatusCreated);
  }

  private TopicTermAggregate selectTerm(String termText, DateMidnight statusCreated) {
    return selectFromDb(TopicBuilder.STANDART_TOPIC_ID, termText, statusCreated);
  }

  private TopicTermAggregate selectFromDb(BigInteger standartTopicId, String term,
      DateMidnight createdDate) {
    TopicTermAggregate key =
        new TopicTermAggregate(standartTopicId, term, createdDate, 1000,
            TopicTermAggregate.AGGREGATE_TYPE.topic_terms);
    List<TopicTermAggregate> selectAggregationTopicTerm = dao.selectAggregationTopicTerm(key);
    if (selectAggregationTopicTerm == null || selectAggregationTopicTerm.size() == 0) {
      return null;
    }
    if (selectAggregationTopicTerm.size() > 1) {
      System.err.println("Duplicated row");
      throw new DuplicateKeyException("there are could not be");
    }

    return selectAggregationTopicTerm.get(0);
  }

  @AfterClass
  public static void after() {
    dao.cleanupTerms(TopicBuilder.STANDART_TOPIC_ID, TopicTermAggregate.AGGREGATE_TYPE.topic_terms,
        STANDART_TERM);
    dao.cleanupTerms(TopicBuilder.STANDART_TOPIC_ID, TopicTermAggregate.AGGREGATE_TYPE.topic_terms,
        TRIMMED_TERM);

  }
}
