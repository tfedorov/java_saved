/**
 * 
 */
package com.tfedorov.social.twitter.processing.reach;

import java.math.BigInteger;

import com.tfedorov.social.utils.date.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import twitter4j.Status;
import twitter4j.User;

import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate.AGGREGATE_TYPE;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDaoImpl;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EstimatedReachProcessorTest {
  

  private static final Long RETWEET_COUNT = 1L;
  private static final int USER_WHO_RETWEET_FOLLOWERS = 20;
  private static final Integer FOLLOW_FIRST_RETWEET = 32;
  private static final AGGREGATE_TYPE POPULAR_TWEETS_TYPE = TopicTweetAggregate.AGGREGATE_TYPE.popular_tweets;
  private static final Long FROM_USER_ID = 334L;
  private static final Long TWEET_ID = 111l;
  private static final String TWEET_TEXT = "Bulllsh*t Rumor Claims Apple Could Be Looking To Replace Tim Cook http://t.co/PNImZfxaeR via @cultofmac";
  private static final BigInteger TOPIC_ID = new BigInteger("3");
  @Mock
  private Status statusStub;
  @Mock
  private Status retweetedStatusStub;
  @Mock
  private TweetsAggregationDaoImpl daoMock;
  @Mock
  private User tweetedUserStub;
  @Mock
  private User retweetedUserStub;
  
  @InjectMocks
  private EstimatedReachProcessor processor = new EstimatedReachProcessor();
  
  @Test
  public void testProcessImplUpdate() {

    //Mock Status object from tweet4j lib
    Mockito.when(statusStub.getUser()).thenReturn(tweetedUserStub);
    Mockito.when(retweetedStatusStub.getId()).thenReturn(TWEET_ID);
    Mockito.when(retweetedStatusStub.getUser()).thenReturn(retweetedUserStub);
    Mockito.when(retweetedStatusStub.getRetweetCount()).thenReturn(1L);
    Mockito.when(tweetedUserStub.getFollowersCount()).thenReturn(USER_WHO_RETWEET_FOLLOWERS);
    Mockito.when(retweetedUserStub.getId()).thenReturn(FROM_USER_ID);
    Mockito.when(retweetedUserStub.getScreenName()).thenReturn("TestedScreenName");
    Mockito.when(retweetedUserStub.getFollowersCount()).thenReturn(FOLLOW_FIRST_RETWEET);
    Mockito.when(retweetedUserStub.getProfileImageURL()).thenReturn("http://a0.twimg.com/profile_images/1639340523/morecash_normal.jpg");
    Mockito.when(retweetedUserStub.getProfileImageURLHttps()).thenReturn("https://si0.twimg.com/profile_images/1090063898/splash_normal.jpg");
    Mockito.when(retweetedUserStub.getProfileImageURLHttps()).thenReturn("https://si0.twimg.com/profile_images/1090063898/splash_normal.jpg");
    Mockito.when(retweetedStatusStub.getCreatedAt()).thenReturn(DateUtils.getCurrentDateTime().minusDays(1).toDate());
        
    // Update method return 1, this mean there are such field in the base
    Mockito.when(daoMock.updateCalcEstimadReach(Mockito.argThat(new TopicTweetAggregateMatcher()),
      Mockito.eq(POPULAR_TWEETS_TYPE),
      Mockito.eq(FOLLOW_FIRST_RETWEET.longValue()))).thenReturn(1);
    
    //under test method
    processor.processTweet(TOPIC_ID, statusStub, retweetedStatusStub, TWEET_TEXT, daoMock, POPULAR_TWEETS_TYPE, null);
    
     Mockito.verify(daoMock).updateCalcEstimadReach(Mockito.argThat(new TopicTweetAggregateMatcher()),
       Mockito.eq(POPULAR_TWEETS_TYPE),
       Mockito.eq(FOLLOW_FIRST_RETWEET.longValue()));

    Mockito.verifyNoMoreInteractions(daoMock);
  }
  
  @Test
  public void testProcessImplInsert() {

    //Mock Status object from tweet4j lib
    Mockito.when(statusStub.getUser()).thenReturn(tweetedUserStub);
    Mockito.when(retweetedStatusStub.getId()).thenReturn(TWEET_ID);
    Mockito.when(retweetedStatusStub.getUser()).thenReturn(retweetedUserStub);
    Mockito.when(retweetedStatusStub.getRetweetCount()).thenReturn(RETWEET_COUNT);
    Mockito.when(tweetedUserStub.getFollowersCount()).thenReturn(USER_WHO_RETWEET_FOLLOWERS);
    Mockito.when(retweetedUserStub.getId()).thenReturn(FROM_USER_ID);
    Mockito.when(retweetedUserStub.getScreenName()).thenReturn("TestedScreenName");
    Mockito.when(retweetedUserStub.getFollowersCount()).thenReturn(FOLLOW_FIRST_RETWEET);
    Mockito.when(retweetedUserStub.getProfileImageURL()).thenReturn("http://a0.twimg.com/profile_images/1639340523/morecash_normal.jpg");
    Mockito.when(retweetedUserStub.getProfileImageURLHttps()).thenReturn("https://si0.twimg.com/profile_images/1090063898/splash_normal.jpg");
    Mockito.when(retweetedUserStub.getProfileImageURLHttps()).thenReturn("https://si0.twimg.com/profile_images/1090063898/splash_normal.jpg");
    Mockito.when(retweetedStatusStub.getCreatedAt()).thenReturn(DateUtils.getCurrentDateTime().minusDays(1).toDate());
    
    // Update method return 0, this mean there are NO such field in the base   
    Mockito.when(daoMock.updateCalcEstimadReach(Mockito.argThat(new TopicTweetAggregateMatcher()),
      Mockito.eq(POPULAR_TWEETS_TYPE),
      Mockito.eq(FOLLOW_FIRST_RETWEET.longValue()))).thenReturn(0);

    //under test method
    processor.processTweet(TOPIC_ID, statusStub, retweetedStatusStub, TWEET_TEXT, daoMock, POPULAR_TWEETS_TYPE, null);
    
     Mockito.verify(daoMock).updateCalcEstimadReach(Mockito.argThat(new TopicTweetAggregateMatcher()),
       Mockito.eq(POPULAR_TWEETS_TYPE),
       Mockito.eq(FOLLOW_FIRST_RETWEET.longValue()));
     
     //if there are row for update method should make insert to database 
     Mockito.verify(daoMock).insertAggregationTopicTweet(Mockito.argThat(new TopicTweetAggregateFullMatcher(null)),
       Mockito.eq(POPULAR_TWEETS_TYPE));

    Mockito.verifyNoMoreInteractions(daoMock);
  }
  
  @Test
  public void testProcessImplInsertRetweetedText() {

    //Mock Status object from tweet4j lib
    Mockito.when(statusStub.getUser()).thenReturn(tweetedUserStub);
    Mockito.when(retweetedStatusStub.getId()).thenReturn(TWEET_ID);
    Mockito.when(retweetedStatusStub.getUser()).thenReturn(retweetedUserStub);
    Mockito.when(retweetedStatusStub.getRetweetCount()).thenReturn(RETWEET_COUNT);
    Mockito.when(tweetedUserStub.getFollowersCount()).thenReturn(USER_WHO_RETWEET_FOLLOWERS);
    Mockito.when(retweetedUserStub.getId()).thenReturn(FROM_USER_ID);
    Mockito.when(retweetedUserStub.getScreenName()).thenReturn("TestedScreenName");
    Mockito.when(retweetedUserStub.getFollowersCount()).thenReturn(FOLLOW_FIRST_RETWEET);
    Mockito.when(retweetedUserStub.getProfileImageURL()).thenReturn("http://a0.twimg.com/profile_images/1639340523/morecash_normal.jpg");
    Mockito.when(retweetedUserStub.getProfileImageURLHttps()).thenReturn("https://si0.twimg.com/profile_images/1090063898/splash_normal.jpg");
    Mockito.when(retweetedUserStub.getProfileImageURLHttps()).thenReturn("https://si0.twimg.com/profile_images/1090063898/splash_normal.jpg");
    Mockito.when(retweetedStatusStub.getCreatedAt()).thenReturn(DateUtils.getCurrentDateTime().minusDays(1).toDate());
    
    // Update method return 0, this mean there are NO such field in the base   
    Mockito.when(daoMock.updateCalcEstimadReach(Mockito.argThat(new TopicTweetAggregateMatcher()),
      Mockito.eq(POPULAR_TWEETS_TYPE),
      Mockito.eq(FOLLOW_FIRST_RETWEET.longValue()))).thenReturn(0);

    //under test method
    processor.processTweet(TOPIC_ID, statusStub, retweetedStatusStub, TWEET_TEXT, daoMock, POPULAR_TWEETS_TYPE, "retweetedText");
    
     Mockito.verify(daoMock).updateCalcEstimadReach(Mockito.argThat(new TopicTweetAggregateMatcher()),
       Mockito.eq(POPULAR_TWEETS_TYPE),
       Mockito.eq(FOLLOW_FIRST_RETWEET.longValue()));
     
     //if there are row for update method should make insert to database 
     Mockito.verify(daoMock).insertAggregationTopicTweet(Mockito.argThat(new TopicTweetAggregateFullMatcher("retweetedText")),
       Mockito.eq(POPULAR_TWEETS_TYPE));

    Mockito.verifyNoMoreInteractions(daoMock);
  }
  
  private class TopicTweetAggregateMatcher extends ArgumentMatcher<TopicTweetAggregate> {

    @Override
    public boolean matches(Object argument) {
      if (argument instanceof TopicTweetAggregate) {
        TopicTweetAggregate arg = (TopicTweetAggregate) argument;
        if (!TOPIC_ID.equals(arg.getTopicId())) return false;

        // Argument match with expected
        return true;

      }
      // should not executed
      return false;
    }

  }
  
  private class TopicTweetAggregateFullMatcher extends ArgumentMatcher<TopicTweetAggregate> {
    
    
    private String retweetedText;

    public TopicTweetAggregateFullMatcher(String retweetedText) {
        this.retweetedText = retweetedText;
    }

    @Override
    public boolean matches(Object argument) {
      if (argument instanceof TopicTweetAggregate) {
        TopicTweetAggregate arg = (TopicTweetAggregate) argument;
        if (!TOPIC_ID.equals(arg.getTopicId())) return false;
        
        if (!TWEET_ID.toString().equals(arg.getTweetId())) return false;
        
        if (FROM_USER_ID != arg.getFromUserId().longValue()) return false;
        
        if(retweetedText != null){
          if (!retweetedText.equals(arg.getText())) return false;
        }else{
          if (!TWEET_TEXT.equals(arg.getText())) return false;
        }
        
        // Estimated field  
 
        // followers_sum
        if (USER_WHO_RETWEET_FOLLOWERS != arg.getFollowersSum()) return false;
        
        // recent_retweets_count
        if (1l != arg.getRecentRetweets()) return false;

        // retweets_count
        if (!RETWEET_COUNT.equals(arg.getRetweets())) return false;

        //(followers_sum/recent_retweets_count)*retweets_count + original_tweet_user_follower
        //(20/1)*1 + 32 = 52
        if (52l != arg.getEstimatedReach()) return false;
        

        // Argument match with expected
        return true;

      }
      // should not executed
      return false;
    }

  }
  
  @Test
  public void testProcessImplOldRetweet() {
    Mockito.when(retweetedStatusStub.getCreatedAt()).thenReturn(DateUtils.getCurrentDateTime().minusDays(111).toDate());
    processor.processTweet(TOPIC_ID, statusStub, retweetedStatusStub, TWEET_TEXT, daoMock, POPULAR_TWEETS_TYPE, null);
    Mockito.verifyNoMoreInteractions(daoMock);
  }
}
