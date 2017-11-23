package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;
import java.util.List;

import com.tfedorov.social.utils.date.DateUtils;
import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate.AGGREGATE_TYPE;

public class TweetsAggregationDaoIntegrationTest {

	private static TweetsAggregationDaoImpl tweetAggregationDao;

	private static DriverManagerDataSource dataSource;

    private static final long TERM_COUNT_BASIC = 423l;
    private static final long TERM_COUNT_BASIC2 = 54231;

	private static final BigInteger TOPIC_ID = new BigInteger("1");
	private static final BigInteger TWEET_ID = new BigInteger("2");	
	private static final BigInteger USER_ID = new BigInteger("3");

    private static final String TESTING_TERM = "term";
    private static final String TESTING_TERM2 = "testing";
    private static final String TESTING_TERMS = "testing term";

	@BeforeClass
	public static void setUp() {
		dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
		dataSource.setUsername("dmp");
		dataSource.setPassword("dmp01");

		tweetAggregationDao = new TweetsAggregationDaoImpl();
		tweetAggregationDao.setDataSource(dataSource);
		System.out.println("datasource initiated...");
	}

	@Test
	public void testTweetsAggregationDao() {
		
		//Clean up data
      tweetAggregationDao.cleanupTerms(TOPIC_ID, TopicTermAggregate.AGGREGATE_TYPE.topic_terms);
      tweetAggregationDao.cleanupTerms(TOPIC_ID, TopicTermAggregate.AGGREGATE_TYPE.topic_bi_terms);
		
		DateMidnight date = DateUtils.getCurrentMidnight();

		TopicTermAggregate termBean = new TopicTermAggregate(TOPIC_ID, TESTING_TERM, date, TERM_COUNT_BASIC, AGGREGATE_TYPE.topic_terms);

		tweetAggregationDao.insertAggregationTopicTerm(termBean, TWEET_ID);

		List<TopicTermAggregate> selectedTopicTerm = tweetAggregationDao.selectAggregationTopicTerm(termBean);

		Assert.assertEquals(1, selectedTopicTerm.size());
		TopicTermAggregate termBeanAfterSaving = selectedTopicTerm.get(0);

		Assert.assertEquals(date.getMillis(), termBeanAfterSaving.getDate().getMillis());
		Assert.assertEquals(TOPIC_ID, termBeanAfterSaving.getTopicId());
		Assert.assertEquals(TESTING_TERM, termBeanAfterSaving.getTerm());
		Assert.assertEquals(TERM_COUNT_BASIC, termBeanAfterSaving.getTermsCount());

		tweetAggregationDao.updateAggregationTopicTermCalc(termBean,TWEET_ID);

		selectedTopicTerm = tweetAggregationDao.selectAggregationTopicTerm(termBean);

		Assert.assertEquals(1, selectedTopicTerm.size());

		TopicTermAggregate termBeanAfterUpdating = selectedTopicTerm.get(0);
		Assert.assertEquals(TERM_COUNT_BASIC + 1, termBeanAfterUpdating.getTermsCount());

        TopicTermAggregate termBean2 = new TopicTermAggregate(TOPIC_ID, TESTING_TERM2, date, TERM_COUNT_BASIC2, AGGREGATE_TYPE.topic_terms);
        tweetAggregationDao.insertAggregationTopicTerm(termBean2, TWEET_ID);

        termBean2 = new TopicTermAggregate(TOPIC_ID, TESTING_TERMS, date, TERM_COUNT_BASIC2, AGGREGATE_TYPE.topic_bi_terms);
        tweetAggregationDao.insertAggregationTopicTerm(termBean2, TWEET_ID);

	}
	@Test
	public void testInsertTweet() {

		tweetAggregationDao.cleanupTweets(TOPIC_ID, TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets);

		DateMidnight date = DateUtils.getCurrentMidnight();

		TopicTweetAggregate topicTweet = new TopicTweetAggregate(TOPIC_ID, TWEET_ID, "tweetText", USER_ID, "retweetedUser.getScreenName()",
				"retweetedUser.getProfileImageURL()", "retweetedUser.getProfileImageURLHttps()", date, 1, 2, 3, 4,
				TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets);

		tweetAggregationDao.insertAggregationTopicTweet(topicTweet, TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets);

		List<TopicTweetAggregate> selectAggregationTopicTweet = tweetAggregationDao.selectAggregationTopicTweet(topicTweet,
				TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets);

		Assert.assertEquals(1, selectAggregationTopicTweet.size());		
		
		TopicTweetAggregate topicTweetAggregateAfterBD = selectAggregationTopicTweet.get(0);
		Assert.assertEquals(TOPIC_ID, topicTweetAggregateAfterBD.getTopicId());
		Assert.assertEquals(TWEET_ID.toString(), topicTweetAggregateAfterBD.getTweetId());
		Assert.assertEquals(date.getMillis(), topicTweetAggregateAfterBD.getCeatedAt().getMillis());
		Assert.assertEquals(3, topicTweetAggregateAfterBD.getFollowersSum());
		
		topicTweet.setFollowersSum(1000);
		
		tweetAggregationDao.updateAggregationTopicTweet(topicTweet,	TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets);
		
		selectAggregationTopicTweet = tweetAggregationDao.selectAggregationTopicTweet(topicTweet,
				TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets);
		
		Assert.assertEquals(1, selectAggregationTopicTweet.size());		
		
		topicTweetAggregateAfterBD = selectAggregationTopicTweet.get(0);
		Assert.assertEquals(TOPIC_ID, topicTweetAggregateAfterBD.getTopicId());
		Assert.assertEquals(1000, topicTweetAggregateAfterBD.getFollowersSum());

		selectAggregationTopicTweet = tweetAggregationDao.selectAggregationTopicTweetByFilter(TOPIC_ID, 0, 1, DateUtils.getIntervalToToday(1000), "retweets", true,
				TopicTweetAggregate.AGGREGATE_TYPE.intention_tweets);
		
		Assert.assertEquals(1, selectAggregationTopicTweet.size());		
		
		topicTweetAggregateAfterBD = selectAggregationTopicTweet.get(0);
		Assert.assertEquals(TOPIC_ID, topicTweetAggregateAfterBD.getTopicId());		
	}



}