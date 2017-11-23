package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.Interval;
import org.joda.time.base.BaseDateTime;

import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate.AGGREGATE_TYPE;
import com.tfedorov.social.twitter.sentiments.SENTIMENT;

public interface TweetsAggregationDao {

  public int insertAggregationTopicMention(TopicMentionAggregate topicMention, SENTIMENT sentiment);

  public int updateAggregationTopicMention(TopicMentionAggregate topicMention);

  public int updateAggTopicMentionIncremently(BigInteger topicId, BaseDateTime date,
      SENTIMENT sentiment);

  public List<TopicMentionAggregate> selectAggregationTopicMention(TopicMentionAggregate key);

  public int insertAggregationTopicTerm(TopicTermAggregate topicTerm, BigInteger tweetId);

  public int updateAggregationTopicTermCalc(TopicTermAggregate topicTerm, BigInteger tweetId);

  public List<TopicTermAggregate> selectAggregationTopicTerm(TopicTermAggregate key);

  public int insertAggregationTopicTweet(TopicTweetAggregate topicTweet,
      TopicTweetAggregate.AGGREGATE_TYPE type);

  public int updateAggregationTopicTweet(TopicTweetAggregate topicTweet,
      TopicTweetAggregate.AGGREGATE_TYPE type);

  public int updateCalcEstimadReach(TopicTweetAggregate topicTweet,
      TopicTweetAggregate.AGGREGATE_TYPE type, long retweetedUserFollowers);

  public int cleanupTweetsById(BigInteger topicId, String tweetId,
      TopicTweetAggregate.AGGREGATE_TYPE type);

  public List<TopicTweetAggregate> selectByTweetAndTopicId(BigInteger topicId, String tweetId,
      final TopicTweetAggregate.AGGREGATE_TYPE type);

  public List<TopicTweetAggregate> selectAggregationTopicTweet(TopicTweetAggregate key,
      TopicTweetAggregate.AGGREGATE_TYPE type);

  public List<BigInteger> checkTableForStatus(BigInteger topicId,
      TweetsAggregationDao.TABLE_NAME tableName);

  public List<BigInteger> checkForTrackedUsers(BigInteger userId,
      TopicTweetAggregate.AGGREGATE_TYPE type);

  public static enum TABLE_NAME {
    topic_terms, topic_bi_terms, topic_tri_terms, topic_mentions, popular_tweets, intention_tweets, keyword_intention_tweets, latest_tweets, topic_terms_p, topic_bi_terms_p, topic_tri_terms_p
  }

  @Deprecated
  public int cleanupTweets(BigInteger topicId, AGGREGATE_TYPE type);

  @Deprecated
  public int cleanupTweets(List<BigInteger> listTweets, BigInteger topicId, AGGREGATE_TYPE type);

  public int cleanupTerms(BigInteger topicId, TopicTermAggregate.AGGREGATE_TYPE type);

  public int cleanupTerms(BigInteger topicId, TopicTermAggregate.AGGREGATE_TYPE type, String term);

  public int cleanupMentions(BigInteger topicId, TABLE_NAME topicMentions);

  public int cleanupAllAgregation(BigInteger topicId);

  public int cleanupAgregationByDates(BaseDateTime upTo);

  public List<TopicTweetAggregate> selectAggregationTopicTweetByFilter(BigInteger topicId,
      int offset, int limit, Interval aggregationTimeInterval, String orderBy, boolean isDesc,
      TopicTweetAggregate.AGGREGATE_TYPE type);

  public List<TopicMentionAggregate> selectAggregationTopicMentionByFilter(BigInteger topicId,
      Interval aggregationTimeInterval);

  public int cleanupPopularByRetweetsAndDates(int popularRetweetsCount, BaseDateTime upToDate,
      AGGREGATE_TYPE type);

  @Deprecated
  List<BigInteger> selectLatestTweetsByTopic(BigInteger topicId, int limit,
      BaseDateTime baseDateTime);

  int cleanLatestTweets(BigInteger topicId, int limit, BaseDateTime baseDateTime);

  List<Integer> loadTopicIdsWithCountsMoreThan(int maxPopularCount, TABLE_NAME table);

  int deleteTweetByMaxCount(int topicId, int maxPopularCount, TABLE_NAME table);
}
