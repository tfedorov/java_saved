/**
 * 
 */
package com.tfedorov.social.twitter.processing.mention;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.base.BaseDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import twitter4j.Status;

import com.tfedorov.social.topic.processing.TopicProcessingContext;
import com.tfedorov.social.twitter.aggregation.dao.TopicMentionAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao.TABLE_NAME;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDaoImpl;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextBuilder;
import com.tfedorov.social.twitter.processing.TopicContextBuilder;
import com.tfedorov.social.utils.date.DateUtils;

/**
 * @author tfedorov
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MentionsHandlerIntegrationTest {

  private static final BigInteger TOPIC_ID = new BigInteger("1");

  @Mock
  private Status statusMock;

  private TweetsAggregationDaoImpl dao;

  @InjectMocks
  private MentionsHandler handler = new MentionsHandler(null);

  private BaseDateTime date = DateUtils.getCurrentMidnight();


  @Before
  public void setup() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
    dataSource.setUsername("dmp");
    dataSource.setPassword("dmp01");
    dao = new TweetsAggregationDaoImpl();
    dao.setDataSource(dataSource);

    dao.cleanupMentions(TOPIC_ID, TABLE_NAME.topic_mentions);

  }


  @Test
  public void testProcessImpl() {

    GeneralProcessingContext context = buildContext();

    Mockito.when(statusMock.getCreatedAt()).thenReturn(new Date());

    handler.processImpl(context);
    List<TopicMentionAggregate> selectedList = getMentionsList();
    Assert.assertEquals(1, selectedList.size());

    TopicMentionAggregate inseertedMentions = selectedList.get(0);
    Assert.assertEquals(1, inseertedMentions.getTweetsCount());

    // Assert.assertEquals(new Long(1), inseertedMentions.getTopic());
    Assert.assertEquals(date, inseertedMentions.getDate());

    handler.processImpl(context);

    selectedList = getMentionsList();
    Assert.assertEquals(1, selectedList.size());

    TopicMentionAggregate updatedMentions = selectedList.get(0);
    Assert.assertEquals(2, updatedMentions.getTweetsCount());

    Assert.assertEquals(date, updatedMentions.getDate());

    handler.processImpl(context);

    selectedList = getMentionsList();
    Assert.assertEquals(1, selectedList.size());

    updatedMentions = selectedList.get(0);
    Assert.assertEquals(3, updatedMentions.getTweetsCount());

    Assert.assertEquals(date, updatedMentions.getDate());
  }


  private GeneralProcessingContext buildContext() {

    GeneralProcessingContextBuilder builderContext = new GeneralProcessingContextBuilder();
    builderContext.tweetsAggregationDao = dao;
    GeneralProcessingContext context = builderContext.build(statusMock);

    TopicProcessingContext topicContext = TopicContextBuilder.build(TOPIC_ID, "job");
    context.add(topicContext.getContextName(), topicContext);
    return context;
  }


  private List<TopicMentionAggregate> getMentionsList() {
    TopicMentionAggregate key = new TopicMentionAggregate(TOPIC_ID, date, 0);
    List<TopicMentionAggregate> selectedList = dao.selectAggregationTopicMention(key);
    return selectedList;
  }

  @After
  public void afterTest() {
    dao.cleanupMentions(TOPIC_ID, TABLE_NAME.topic_mentions);
  }

}
