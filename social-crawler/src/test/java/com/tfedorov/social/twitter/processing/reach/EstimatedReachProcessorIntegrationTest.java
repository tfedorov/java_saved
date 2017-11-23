/**
 * 
 */
package com.tfedorov.social.twitter.processing.reach;

import java.math.BigInteger;
import java.util.List;

import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDaoImpl;
import com.tfedorov.social.utils.date.DateUtils;
import junit.framework.Assert;

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
import twitter4j.User;

import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate.AGGREGATE_TYPE;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EstimatedReachProcessorIntegrationTest {
  
  private static final int SECOND_USER_WHO_RETWEET_FOLLOWERS = 27;
  private static final int USER_WHO_RETWEET_FOLLOWERS = 20;
  private static final long RETWEET_COUNT_IN_2_RETWEET = 4L;
  private static final int FOLLOW_FIRST_RETWEET = 32;
  private static final AGGREGATE_TYPE POPULAR_TWEETS_TYPE = TopicTweetAggregate.AGGREGATE_TYPE.popular_tweets;
  private static final int ORIGINAL_TWEET_USER_FOLLOWER = 30;
  private static final Long FROM_USER_ID = 334L;
  private static final Long TWEET_ID = 111l;
  private static final String TWEET_TEXT = "Bulllsh*t Rumor Claims Apple Could Be Looking To Replace Tim Cook http://t.co/PNImZfxaeR via @cultofmac";
  private static final BigInteger TOPIC_ID = new BigInteger("3");
  @Mock
  private Status statusMock;
  @Mock
  private Status retweetedStatusMock;
  //@Mock
  private TweetsAggregationDaoImpl dao;
  @Mock
  private User userMock;
  @Mock
  private User retweetedUserMock;
  
  @InjectMocks
  private EstimatedReachProcessor processor = new EstimatedReachProcessor();

  @Before
  public void setup() {
    dao = new TweetsAggregationDaoImpl();
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
    dataSource.setUsername("dmp");
    dataSource.setPassword("dmp01");
    dao.setDataSource(dataSource);

    dao.cleanupTweetsById(TOPIC_ID, TWEET_ID.toString(), POPULAR_TWEETS_TYPE);
  }
  
  @Test
  public void testProcessImpl() {

    Mockito.when(statusMock.getUser()).thenReturn(userMock);
    Mockito.when(retweetedStatusMock.getId()).thenReturn(TWEET_ID);
    Mockito.when(retweetedStatusMock.getUser()).thenReturn(retweetedUserMock);
    Mockito.when(retweetedStatusMock.getRetweetCount()).thenReturn(1L);
    Mockito.when(userMock.getFollowersCount()).thenReturn(USER_WHO_RETWEET_FOLLOWERS);
    Mockito.when(retweetedUserMock.getId()).thenReturn(FROM_USER_ID);
    Mockito.when(retweetedUserMock.getScreenName()).thenReturn("TestedScreenName");
    Mockito.when(retweetedUserMock.getFollowersCount()).thenReturn(FOLLOW_FIRST_RETWEET);
    Mockito.when(retweetedUserMock.getProfileImageURL()).thenReturn("http://a0.twimg.com/profile_images/1639340523/morecash_normal.jpg");
    Mockito.when(retweetedUserMock.getProfileImageURLHttps()).thenReturn("https://si0.twimg.com/profile_images/1090063898/splash_normal.jpg");
    Mockito.when(retweetedUserMock.getProfileImageURLHttps()).thenReturn("https://si0.twimg.com/profile_images/1090063898/splash_normal.jpg");
    Mockito.when(retweetedStatusMock.getCreatedAt()).thenReturn(DateUtils.getCurrentDateTime().minusDays(1).toDate());
    processor.processTweet(TOPIC_ID, statusMock, retweetedStatusMock, TWEET_TEXT, dao, POPULAR_TWEETS_TYPE, null);

    List<TopicTweetAggregate> selectedRows = dao.selectByTweetAndTopicId(TOPIC_ID, TWEET_ID.toString(), POPULAR_TWEETS_TYPE);
    Assert.assertEquals(1, selectedRows.size());
    
    TopicTweetAggregate insertedRow = selectedRows.get(0);
    Assert.assertEquals(1, insertedRow.getRecentRetweets());
    Assert.assertEquals(USER_WHO_RETWEET_FOLLOWERS,insertedRow.getFollowersSum());
    Assert.assertEquals(1,insertedRow.getRetweets());
    Assert.assertEquals(FROM_USER_ID.toString(),insertedRow.getFromUserId().toString());

    //(followers_sum/recent_retweets_count)*retweets_count + original_tweet_user_follower
    //(20/1)*1 + 32 = 52
    Assert.assertEquals(52,insertedRow.getEstimatedReach());
    
    Mockito.when(retweetedStatusMock.getCreatedAt()).thenReturn(DateUtils.getCurrentDateTime().minusDays(1).toDate());
    Mockito.when(statusMock.getUser()).thenReturn(userMock);
    Mockito.when(retweetedStatusMock.getId()).thenReturn(TWEET_ID);
    Mockito.when(retweetedStatusMock.getUser()).thenReturn(retweetedUserMock);
    Mockito.when(retweetedStatusMock.getRetweetCount()).thenReturn(RETWEET_COUNT_IN_2_RETWEET);
    Mockito.when(userMock.getFollowersCount()).thenReturn(SECOND_USER_WHO_RETWEET_FOLLOWERS);
    Mockito.when(retweetedUserMock.getId()).thenReturn(FROM_USER_ID);
    Mockito.when(retweetedUserMock.getFollowersCount()).thenReturn(ORIGINAL_TWEET_USER_FOLLOWER+2);

    processor.processTweet(TOPIC_ID, statusMock, retweetedStatusMock, TWEET_TEXT, dao, POPULAR_TWEETS_TYPE, null);
    
    selectedRows = dao.selectByTweetAndTopicId(TOPIC_ID, TWEET_ID.toString(), POPULAR_TWEETS_TYPE);
    Assert.assertEquals(1, selectedRows.size());

    TopicTweetAggregate updatedRow = selectedRows.get(0);
    Assert.assertEquals(2, updatedRow.getRecentRetweets());
    Assert.assertEquals(RETWEET_COUNT_IN_2_RETWEET,updatedRow.getRetweets());
    //followers_sum = followers_sum from prev tweets + new
    Assert.assertEquals(USER_WHO_RETWEET_FOLLOWERS + SECOND_USER_WHO_RETWEET_FOLLOWERS,updatedRow.getFollowersSum());
    //(followers_sum/recent_retweets_count)*retweets_count + original_tweet_user_follower
    //(47/2)*4 + 32 = 128
    Assert.assertEquals(128,updatedRow.getEstimatedReach());
  }
  
  @After
  public void afterTest() {
    dao.cleanupTweetsById(TOPIC_ID, TWEET_ID.toString(), POPULAR_TWEETS_TYPE);
  }
  
}
