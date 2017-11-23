package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.tfedorov.social.twitter.aggregation.DaoException;
import com.tfedorov.social.utils.date.DateUtils;
import org.joda.time.Interval;
import org.joda.time.base.BaseDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate.AGGREGATE_TYPE;
import com.tfedorov.social.twitter.sentiments.SENTIMENT;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Repository("tweetsAggregationDao")
public class TweetsAggregationDaoImpl implements TweetsAggregationDao {

  private static final String WHERE_TOPIC_ID_AND_ADATETIME_SUB_QUERY = " where topic_id = ? and adatetime = ? ";
  private static final String DELETE_FROM_SUB_QUERY = "delete from ";
  private static final int TERM_MAX_SIZE = 50;
  private static final int BI_TERM_MAX_SIZE = 100;
  private static final int TRI_TERM_MAX_SIZE = 150;

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetsAggregationDaoImpl.class);

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public enum TABLE_NAME_CREATE_DATE_MAPPING {
    topic_terms("adatetime"), topic_bi_terms("adatetime"), topic_tri_terms("adatetime"), topic_mentions(
        "adatetime"), popular_tweets("created_at"), intention_tweets("created_at"), keyword_intention_tweets(
        "created_at"), latest_tweets("created_at");

    private final String field;

    private TABLE_NAME_CREATE_DATE_MAPPING(String f) {
      field = f;
    }

    public String getField() {
      return field;
    }
  }

  @Autowired
  private DataSource dataSource;

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public int insertAggregationTopicMention(TopicMentionAggregate topicMention, SENTIMENT sentiment) {
    EtmPoint perfPoint = getPerformancePoint(".insertTopicMention()");
    try {
      String sqlQuery;
      if (sentiment == SENTIMENT.neutral) {
        sqlQuery =
            "insert into topic_mentions(topic_id, adatetime, tweets_count, modification_date ) "
                + " values (?, ?, ?, CURRENT_TIMESTAMP)";
      } else {
        sqlQuery =
            "insert into topic_mentions(topic_id, adatetime, tweets_count, modification_date, "
                + sentiment.name() + "_mentions ) " + " values (?, ?, ?, CURRENT_TIMESTAMP,1)";
      }
      JdbcTemplate insert = new JdbcTemplate(dataSource);
      return insert.update(
          sqlQuery,
          new Object[] {topicMention.getTopicId(),
              DateUtils.convertToTimeStamp(topicMention.getDate()), topicMention.getTweetsCount()});
    } catch (DataAccessException e) {
      throw new DaoException(topicMention.toString(), e);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int updateAggregationTopicMention(TopicMentionAggregate topicMention) {
    EtmPoint perfPoint = getPerformancePoint(".updateTopicMention()");
    try {
      String sqlQuery =
          "update topic_mentions set tweets_count = ? , modification_date = CURRENT_TIMESTAMP "
              + WHERE_TOPIC_ID_AND_ADATETIME_SUB_QUERY;

      JdbcTemplate update = new JdbcTemplate(dataSource);
      return update.update(
          sqlQuery,
          new Object[] {topicMention.getTweetsCount(), topicMention.getTopicId(),
              DateUtils.convertToTimeStamp(topicMention.getDate())});
    } catch (DataAccessException e) {
      throw new DaoException(topicMention.toString(), e);
    } finally {
      perfPoint.collect();
    }
  }

  public int updateAggTopicMentionIncremently(BigInteger topicId, BaseDateTime date,
      SENTIMENT sentiment) {
    EtmPoint perfPoint = getPerformancePoint(".updateTopicMention()");
    try {
      String sqlQuery;
      if (sentiment == SENTIMENT.neutral) {
        sqlQuery =
            "update topic_mentions set tweets_count = tweets_count + 1 , modification_date = CURRENT_TIMESTAMP "
                + WHERE_TOPIC_ID_AND_ADATETIME_SUB_QUERY;
      } else {
        sqlQuery =
            "update topic_mentions set tweets_count = tweets_count + 1, " + sentiment.name()
                + "_mentions = " + sentiment.name()
                + "_mentions + 1, modification_date = CURRENT_TIMESTAMP "
                + WHERE_TOPIC_ID_AND_ADATETIME_SUB_QUERY;
      }
      JdbcTemplate update = new JdbcTemplate(dataSource);
      return update.update(sqlQuery, new Object[] {topicId, DateUtils.convertToTimeStamp(date)});

    } catch (DataAccessException e) {
      String msg =
          new StringBuilder("Exception in the data:").append(" topicId = " + topicId)
              .append(" date = " + DateUtils.printDate(date)).toString();
      throw new DaoException(msg, e);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<TopicMentionAggregate> selectAggregationTopicMention(TopicMentionAggregate key) {
    EtmPoint perfPoint = getPerformancePoint(".selectTopicMention()");

    try {
      String sqlQuery =
          "select topic_id, adatetime, tweets_count from topic_mentions"
              + WHERE_TOPIC_ID_AND_ADATETIME_SUB_QUERY;

      JdbcTemplate template = new JdbcTemplate(dataSource);

      return template.query(sqlQuery,
          new Object[] {key.getTopicId(), DateUtils.convertToTimeStamp(key.getDate())},
          new RowMapper<TopicMentionAggregate>() {

            @Override
            public TopicMentionAggregate mapRow(ResultSet rs, int i) throws SQLException {

              TopicMentionAggregate tma =
                  new TopicMentionAggregate(rs.getBigDecimal("topic_id").toBigInteger(), DateUtils
                      .convertToDateTime(rs.getTimestamp("adatetime")), rs.getLong("tweets_count"));
              return tma;
            }

          });
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public int insertAggregationTopicTerm(TopicTermAggregate topicTerm, BigInteger tweetId) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".insertTopicTerm():").append(
            topicTerm.getType().name()).toString());
    try {
      String sqlQuery =
          "insert into " + topicTerm.getType().name()
              + "(topic_id, term, adatetime, terms_count, modification_date, tw1_id ) "
              + " values (?, left(?,?), ?, ?, CURRENT_TIMESTAMP, ?)";

      int charsToCut = getTermMaxValue(topicTerm.getType());

      JdbcTemplate insert = new JdbcTemplate(dataSource);
      return insert.update(sqlQuery, new Object[] {topicTerm.getTopicId(), topicTerm.getTerm(),
          charsToCut, DateUtils.convertToTimeStamp(topicTerm.getDate()), topicTerm.getTermsCount(),
          tweetId});
    } catch (DataAccessException e) {
      throw new DaoException(topicTerm.toString() + " , tweetId = " + tweetId, e);
    } finally {
      perfPoint.collect();
    }
  }

  private int getTermMaxValue(
      TopicTermAggregate.AGGREGATE_TYPE type) {
    int charsToCut = TERM_MAX_SIZE;

    if (type == TopicTermAggregate.AGGREGATE_TYPE.topic_bi_terms) {
      charsToCut = BI_TERM_MAX_SIZE;
    } else if (type == TopicTermAggregate.AGGREGATE_TYPE.topic_tri_terms) {
      charsToCut = TRI_TERM_MAX_SIZE;
    }
    return charsToCut;
  }


  @Override
  public int updateAggregationTopicTermCalc(TopicTermAggregate topicTerm, BigInteger tweetId) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".updateTopicTerm():").append(
            topicTerm.getType().name()).toString());
    try {
      String sqlQuery =
          "update "
              + topicTerm.getType().name()
              + " set terms_count =  terms_count + 1 , modification_date = CURRENT_TIMESTAMP, tw3_id = tw2_id, tw2_id = tw1_id  ,tw1_id = ? "
              + " where topic_id = ? and term = left(?,?) and adatetime = ? ";

      int charsToCut = getTermMaxValue(topicTerm.getType());
      JdbcTemplate update = new JdbcTemplate(dataSource);
      return update.update(
          sqlQuery,
          new Object[] {tweetId, topicTerm.getTopicId(), topicTerm.getTerm(), charsToCut,
              DateUtils.convertToTimeStamp(topicTerm.getDate())});
    } catch (DataAccessException e) {
      throw new DaoException(topicTerm.toString() + " , tweetId = " + tweetId, e);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<TopicTermAggregate> selectAggregationTopicTerm(final TopicTermAggregate key) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".selectTopicTerm():").append(key.getType().name())
            .toString());

    try {
      String sqlQuery =
          "select topic_id, term, adatetime, terms_count, tw1_id, tw2_id, tw3_id from "
              + key.getType().name() + " where topic_id = ? and term = ? and adatetime = ? ";

      JdbcTemplate template = new JdbcTemplate(dataSource);

      return template.query(
          sqlQuery,
          new Object[] {key.getTopicId(), key.getTerm(),
              DateUtils.convertToTimeStamp(key.getDate())}, new RowMapper<TopicTermAggregate>() {

            public TopicTermAggregate mapRow(ResultSet rs, int i) throws SQLException {
              TopicTermAggregate tta =
                  new TopicTermAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
                      .getString("term"),
                      DateUtils.convertToDateTime(rs.getTimestamp("adatetime")), rs
                          .getLong("terms_count"), key.getType(), rs.getBigDecimal("tw1_id"), rs
                          .getBigDecimal("tw2_id"), rs.getBigDecimal("tw3_id"));
              return tta;

            }

          });
    } finally {
      perfPoint.collect();
    }

  }


  @Override
  public int insertAggregationTopicTweet(TopicTweetAggregate topicTweet,
      TopicTweetAggregate.AGGREGATE_TYPE type) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".insertTopicTweet():").append(type.name())
            .toString());
    try {
      String sqlQuery =
          "insert into "
              + type.name()
              + "(topic_id, tweet_id, text, from_user_id, from_user, profile_image_url, profile_image_url_https, "
              + "created_at, retweets_count, recent_retweets_count, followers_sum, estimated_reach, modification_date) "
              + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

      JdbcTemplate insert = new JdbcTemplate(dataSource);

      return insert.update(
          sqlQuery,
          new Object[] {topicTweet.getTopicId(), topicTweet.getTweetId(), topicTweet.getText(),
              topicTweet.getFromUserId(), topicTweet.getFromUser(),
              topicTweet.getProfileImageUrl(), topicTweet.getProfileImageUrlHttps(),
              DateUtils.convertToTimeStamp(topicTweet.getCeatedAt()), topicTweet.getRetweets(),
              topicTweet.getRecentRetweets(), topicTweet.getFollowersSum(),
              topicTweet.getEstimatedReach()});
    } catch (DataAccessException e) {
      throw new DaoException(topicTweet.toString() + " , type = " + type, e);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int updateAggregationTopicTweet(TopicTweetAggregate topicTweet,
      TopicTweetAggregate.AGGREGATE_TYPE type) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".updateTopicTweet():").append(type.name())
            .toString());
    try {
      String sqlQuery =
          "update "
              + type.name()
              + " set retweets_count = ? , recent_retweets_count = ? , followers_sum = ? , estimated_reach = ? ,"
              + " modification_date = CURRENT_TIMESTAMP " + " where topic_id = ? and tweet_id = ? ";

      JdbcTemplate update = new JdbcTemplate(dataSource);

      return update.update(
          sqlQuery,
          new Object[] {topicTweet.getRetweets(), topicTweet.getRecentRetweets(),
              topicTweet.getFollowersSum(), topicTweet.getEstimatedReach(),
              topicTweet.getTopicId(), topicTweet.getTweetId()});
    } catch (DataAccessException e) {
      throw new DaoException(topicTweet.toString() + " , type = " + type, e);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int updateCalcEstimadReach(TopicTweetAggregate topicTweet,
      TopicTweetAggregate.AGGREGATE_TYPE type, long retweetedUserFollowers) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".updateTopicTweet():").append(type.name())
            .toString());
    try {
      String sqlQuery =
          "update "
              + type.name()
              + " set retweets_count = ?,"
              + "recent_retweets_count = recent_retweets_count + 1 , followers_sum = followers_sum + ? ,"
              + "estimated_reach = ROUND(followers_sum/recent_retweets_count)*retweets_count + ? ,"
              + " modification_date = CURRENT_TIMESTAMP " + " where tweet_id = ? and topic_id =?;";

      JdbcTemplate update = new JdbcTemplate(dataSource);

      return update.update(sqlQuery,
          new Object[] {topicTweet.getRetweets(), topicTweet.getFollowersSum(),
              retweetedUserFollowers, topicTweet.getTweetId(), topicTweet.getTopicId()});
    } catch (DataAccessException e) {
      throw new DaoException(topicTweet.toString() + " , type = " + type
          + ", retweetedUserFollowers = " + retweetedUserFollowers, e);
    } finally {
      perfPoint.collect();
    }
  }

  /**
   * Test purposes
   */
  @Override
  public int cleanupTweetsById(BigInteger topicId, String tweetId,
      TopicTweetAggregate.AGGREGATE_TYPE type) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".cleanupTweet by id ():").append(type.name())
            .toString());
    try {
      String sqlQuery = DELETE_FROM_SUB_QUERY + type.name() + " where topic_id = ? and tweet_id = ?";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(sqlQuery, new Object[] {topicId, tweetId});
    } finally {
      perfPoint.collect();
    }
  }

  /**
   * Test purposes
   */
  @Override
  public List<TopicTweetAggregate> selectByTweetAndTopicId(BigInteger topicId, String tweetId,
      final TopicTweetAggregate.AGGREGATE_TYPE type) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".checkForTrackedUsers():").append(type.name())
            .toString());
    try {
      String sqlQuery =
          "select text, created_at, topic_id, tweet_id, from_user_id , recent_retweets_count, retweets_count, estimated_reach , followers_sum from "
              + type.name() + "  where topic_id = ? and tweet_id = ? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);

      List<TopicTweetAggregate> topicIds = template.query(sqlQuery, new Object[] {topicId, tweetId

      }, new RowMapper<TopicTweetAggregate>() {
        @Override
        public TopicTweetAggregate mapRow(ResultSet rs, int i) throws SQLException {
          TopicTweetAggregate ttwa =
              new TopicTweetAggregate(rs.getString("text"), DateUtils.convertToDateTime(rs
                  .getTimestamp("created_at")), rs.getBigDecimal("topic_id").toBigInteger(), rs
                  .getBigDecimal("tweet_id").toBigInteger(), rs.getBigDecimal("from_user_id")
                  .toBigInteger(), rs.getLong("recent_retweets_count"), rs
                  .getLong("retweets_count"), rs.getLong("estimated_reach"), rs
                  .getLong("followers_sum"));
          return ttwa;
        }

      });

      return topicIds;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<TopicTweetAggregate> selectAggregationTopicTweet(TopicTweetAggregate key,
      final TopicTweetAggregate.AGGREGATE_TYPE type) {

    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".selectTopicTweet():").append(type.name())
            .toString());
    try {
      String sqlQuery =
          "select topic_id, tweet_id, text, from_user_id, from_user, profile_image_url, profile_image_url_https, "
              + "created_at, retweets_count, recent_retweets_count, followers_sum, estimated_reach from "
              + type.name() + " where topic_id = ? and tweet_id = ? ";

      JdbcTemplate template = new JdbcTemplate(dataSource);

      return template.query(sqlQuery, new Object[] {key.getTopicId(), key.getTweetId()},
          new RowMapper<TopicTweetAggregate>() {

            @Override
            public TopicTweetAggregate mapRow(ResultSet rs, int i) throws SQLException {
              TopicTweetAggregate ttwa =
                  new TopicTweetAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
                      .getBigDecimal("tweet_id").toBigInteger(), rs.getString("text"), rs
                      .getBigDecimal("from_user_id").toBigInteger(), rs.getString("from_user"), rs
                      .getString("profile_image_url"), rs.getString("profile_image_url_https"),
                      DateUtils.convertToDateTime(rs.getTimestamp("created_at")), rs
                          .getLong("retweets_count"), rs.getLong("recent_retweets_count"), rs
                          .getLong("followers_sum"), rs.getLong("estimated_reach"), type);
              return ttwa;
            }

          });
    } finally {
      perfPoint.collect();
    }
  }



  @Override
  public List<BigInteger> checkTableForStatus(BigInteger topicId,
      TweetsAggregationDao.TABLE_NAME tableName) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".checkTableForStatus():").append(tableName.name())
            .toString());
    try {
      String sqlQuery = "select topic_id from " + tableName + " where topic_id = ? limit 1";
      JdbcTemplate template = new JdbcTemplate(dataSource);

      List<BigInteger> topicIds =
          template.query(sqlQuery, new Object[] {topicId}, new RowMapper<BigInteger>() {
            @Override
            public BigInteger mapRow(ResultSet rs, int i) throws SQLException {
              return rs.getBigDecimal("topic_id").toBigInteger();
            }
          });

      return topicIds;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<BigInteger> checkForTrackedUsers(BigInteger userId,
      TopicTweetAggregate.AGGREGATE_TYPE type) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".checkForTrackedUsers():").append(type.name())
            .toString());
    try {
      String sqlQuery = "select distinct topic_id from " + type.name() + " where from_user_id = ? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);

      List<BigInteger> topicIds = template.query(sqlQuery, new Object[] {userId

      }, new RowMapper<BigInteger>() {

        @Override
        public BigInteger mapRow(ResultSet rs, int i) throws SQLException {
          return rs.getBigDecimal("topic_id").toBigInteger();
        }
      });

      return topicIds;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int cleanupTweets(BigInteger topicId, TopicTweetAggregate.AGGREGATE_TYPE type) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".cleanupTweets():").append(type.name()).toString());
    try {
      String sqlQuery = DELETE_FROM_SUB_QUERY + type.name() + " where topic_id = ? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(sqlQuery, new Object[] {topicId});
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int cleanupTerms(BigInteger topicId, TopicTermAggregate.AGGREGATE_TYPE type) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".cleanupTerms():").append(type.name()).toString());
    try {
      String sqlQuery = DELETE_FROM_SUB_QUERY + type.name() + " where topic_id = ? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(sqlQuery, new Object[] {topicId});
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int cleanupTerms(BigInteger topicId, TopicTermAggregate.AGGREGATE_TYPE type, String keyWord) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".cleanupTerms(keyWord):").append(type.name())
            .toString());
    try {
      String sqlQuery =
          new StringBuilder(DELETE_FROM_SUB_QUERY).append(type.name()).append(" where topic_id = ? ")
              .append(" AND term = ?").toString();
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(sqlQuery, new Object[] {topicId, keyWord});
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int cleanupMentions(BigInteger topicId, TABLE_NAME topicMentions) {
    EtmPoint perfPoint = getPerformancePoint(".cleanupMentions()");
    try {
      String sqlQuery = DELETE_FROM_SUB_QUERY + TABLE_NAME.topic_mentions + " where topic_id = ? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(sqlQuery, new Object[] {topicId});
    } finally {
      perfPoint.collect();
    }

  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(TweetsAggregationDaoImpl.class
        .toString()).append(name).toString());
  }

  @Override
  public int cleanupAllAgregation(BigInteger topicId) {
    int res = 0;
    EtmPoint perfPoint = getPerformancePoint(".cleanupAll()");
    try {
      for (TABLE_NAME name : TABLE_NAME.values()) {
        res += deleteRowsFromTableByTopicId(topicId, name.name());
      }
      return res;
    } finally {
      perfPoint.collect();
    }

  }

  private int deleteRowsFromTableByTopicId(BigInteger topicId, String tableName) {

    EtmPoint perfPoint = getPerformancePoint(".delete():" + tableName);
    try {
      String sqlQuery = DELETE_FROM_SUB_QUERY + tableName + " where topic_id = ? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(sqlQuery, new Object[] {topicId});
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public int cleanupAgregationByDates(BaseDateTime upTo) {
    int res = 0;
    EtmPoint perfPoint = getPerformancePoint(".cleanupAgregationByDates()");
    try {
      for (TABLE_NAME_CREATE_DATE_MAPPING name : TABLE_NAME_CREATE_DATE_MAPPING.values()) {
        res += deleteRowsFromTableByDate(name, upTo);
      }
      return res;
    } finally {
      perfPoint.collect();
    }
  }

  private int deleteRowsFromTableByDate(TABLE_NAME_CREATE_DATE_MAPPING name, BaseDateTime upTo) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".deleteRowsFromTableByTopicsDate():").append(name)
            .toString());
    int res = 0;
    try {
      String sqlQuery = DELETE_FROM_SUB_QUERY + name + " where " + name.getField() + " < ?";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      res += template.update(sqlQuery, new Object[] {DateUtils.convertToTimeStamp(upTo)});
      LOGGER.debug("RUN QUERY: "
          + sqlQuery.replace("?", DateUtils.convertToTimeStamp(upTo).toString()));
    } finally {
      perfPoint.collect();
    }
    return res;
  }

  @Override
  public List<TopicTweetAggregate> selectAggregationTopicTweetByFilter(BigInteger topicId,
      int offset, int limit, Interval aggregationTimeInterval, String orderBy, boolean isDesc,
      final AGGREGATE_TYPE type) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".selectAggregationTopicTweetByFilter():").append(
            type.name()).toString());
    try {
      String sqlQuery =
          "select text, created_at, topic_id, tweet_id, from_user_id , from_user, retweets_count, estimated_reach from "
              + type.name()
              + " where created_at>=? and created_at<=? and topic_id=?   order by "
              + PopularTweetsSortingFields.valueOf(orderBy).getField()
              + " "
              + (isDesc ? "desc" : "asc")
              + ((limit > 0 && offset >= 0) ? (" limit " + limit + " offset " + offset) : "");
      JdbcTemplate template = new JdbcTemplate(dataSource);

      return template.query(
          sqlQuery,
          new Object[] {DateUtils.convertToTimeStamp(aggregationTimeInterval.getStart()),
              DateUtils.convertToTimeStamp(aggregationTimeInterval.getEnd()), topicId},
          new RowMapper<TopicTweetAggregate>() {
            @Override
            public TopicTweetAggregate mapRow(ResultSet rs, int i) throws SQLException {
              TopicTweetAggregate ttwa =
                  new TopicTweetAggregate(rs.getString("text"), DateUtils.convertToDateTime(rs
                      .getTimestamp("created_at")), rs.getBigDecimal("topic_id").toBigInteger(), rs
                      .getBigDecimal("tweet_id").toBigInteger(), rs.getBigDecimal("from_user_id")
                      .toBigInteger(), rs.getString("from_user"), rs.getLong("retweets_count"), rs
                      .getLong("estimated_reach"), type);
              return ttwa;
            }

          });

    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<TopicMentionAggregate> selectAggregationTopicMentionByFilter(BigInteger topicId,
      Interval aggregationTimeInterval) {
    EtmPoint perfPoint = getPerformancePoint(".selectTopicMention()");

    try {
      String sqlQuery =
          "select topic_id, adatetime, tweets_count, positive_mentions, negative_mentions from topic_mentions"
              + " where topic_id = ? and adatetime >= ? and  adatetime <= ? order by adatetime";

      JdbcTemplate template = new JdbcTemplate(dataSource);

      return template.query(sqlQuery,
          new Object[] {topicId, DateUtils.convertToTimeStamp(aggregationTimeInterval.getStart()),
              DateUtils.convertToTimeStamp(aggregationTimeInterval.getEnd())},
          new RowMapper<TopicMentionAggregate>() {

            @Override
            public TopicMentionAggregate mapRow(ResultSet rs, int i) throws SQLException {

              TopicMentionAggregate tma =
                  new TopicMentionAggregate(rs.getBigDecimal("topic_id").toBigInteger(), DateUtils
                      .convertToDateTime(rs.getTimestamp("adatetime")), rs.getLong("tweets_count"),
                      rs.getInt("positive_mentions"), rs.getInt("negative_mentions"));
              return tma;
            }

          });
    } finally {
      perfPoint.collect();
    }

  }

  enum PopularTweetsSortingFields {
    text("text"), retweets("retweets_count"), replies("replies"), reach("estimated_reach");
    private final String field;

    private PopularTweetsSortingFields(String f) {
      field = f;
    }

    public String getField() {
      return field;
    }
  }

  @Override
  public int cleanupPopularByRetweetsAndDates(int popularRetweetsCount, BaseDateTime upToDate,
      AGGREGATE_TYPE type) {
    EtmPoint perfPoint = getPerformancePoint(".cleanupPopularByRetweetsAndDates():" + type.name());
    try {
      String sqlQuery =
          DELETE_FROM_SUB_QUERY + type.name() + " where retweets_count <=? and modification_date<?";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(sqlQuery,
          new Object[] {popularRetweetsCount, DateUtils.convertToTimeStamp(upToDate)});
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int cleanupTweets(List<BigInteger> listTweets, BigInteger topicId, AGGREGATE_TYPE type) {
    EtmPoint perfPoint = getPerformancePoint(".cleanupTweetById():" + type.name());
    try {
      String sqlQuery =
          DELETE_FROM_SUB_QUERY + type.name()
              + " where tweet_id in (:tweetsList) and topic_id = (:topicId)";
      NamedParameterJdbcTemplate namedParameterJdbcTemplate =
          new NamedParameterJdbcTemplate(dataSource);

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("tweetsList", listTweets);
      map.put("topicId", topicId);

      return namedParameterJdbcTemplate.update(sqlQuery, map);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<BigInteger> selectLatestTweetsByTopic(BigInteger topicId, int limit,
      BaseDateTime baseDateTime) {
    EtmPoint perfPoint = getPerformancePoint(".selectLatestTweetsByTopic()");

    try {
      String sqlQuery =
          "SELECT tweet_id from latest_tweets "
              + "left join (select tw1_id,tw2_id,tw3_id,term as t1 from topic_terms_p where topic_id=? and adatetime = ?) as terms3 on (latest_tweets.tweet_id = terms3.tw1_id or latest_tweets.tweet_id = terms3.tw2_id or latest_tweets.tweet_id = terms3.tw3_id) "
              + "left join (select tw1_id,tw2_id,tw3_id,term as t2 from topic_bi_terms_p where topic_id=? and adatetime = ?) as terms4 on (latest_tweets.tweet_id = terms4.tw1_id or latest_tweets.tweet_id = terms4.tw2_id or latest_tweets.tweet_id = terms4.tw3_id) "
              + "left join (select tw1_id,tw2_id,tw3_id,term as t3 from topic_tri_terms_p where topic_id=? and adatetime = ?) as terms5 on (latest_tweets.tweet_id = terms5.tw1_id or latest_tweets.tweet_id = terms5.tw2_id or latest_tweets.tweet_id = terms5.tw3_id) "
              + "where latest_tweets.topic_id = ? and t1 is null and t2 is null and t3 is null and latest_tweets.topic_id != latest_tweets.tweet_id limit ?";
      JdbcTemplate template = new JdbcTemplate(dataSource);

      return template.query(sqlQuery, new Object[] {topicId, baseDateTime, topicId, baseDateTime,
          topicId, baseDateTime, topicId, limit}, new RowMapper<BigInteger>() {
        @Override
        public BigInteger mapRow(ResultSet rs, int i) throws SQLException {
          return rs.getBigDecimal("tweet_id").toBigInteger();
        }
      });

    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int cleanLatestTweets(BigInteger topicId, int limit, BaseDateTime baseDateTime) {
    EtmPoint perfPoint = getPerformancePoint(".cleanLatestTweets()");
    Timestamp currentTimestamp = DateUtils.convertToTimeStamp(baseDateTime);
    Timestamp dayBefore = DateUtils.convertToTimeStamp(baseDateTime.toDateTime().minusDays(1));
    try {
      String sqlQuery =
          "delete from latest_tweets where topic_id = ? and tweet_id in (select tweet_id from (SELECT tweet_id from latest_tweets"
              + " left join (select tw1_id,tw2_id,tw3_id,term as t1 from topic_terms_p where topic_id=? and adatetime = ?) as terms3 on (latest_tweets.tweet_id = terms3.tw1_id or latest_tweets.tweet_id = terms3.tw2_id or latest_tweets.tweet_id = terms3.tw3_id)"
              + " left join (select tw1_id,tw2_id,tw3_id,term as t2 from topic_bi_terms_p where topic_id=? and adatetime = ?) as terms4 on (latest_tweets.tweet_id = terms4.tw1_id or latest_tweets.tweet_id = terms4.tw2_id or latest_tweets.tweet_id = terms4.tw3_id)"
              + " left join (select tw1_id,tw2_id,tw3_id,term as t3 from topic_tri_terms_p where topic_id=? and adatetime = ?) as terms5 on (latest_tweets.tweet_id = terms5.tw1_id or latest_tweets.tweet_id = terms5.tw2_id or latest_tweets.tweet_id = terms5.tw3_id)"
              + " where latest_tweets.topic_id = ? and (latest_tweets.created_at >= ? and latest_tweets.created_at < ?) and t1 is null and t2 is null and t3 is null limit ?) as t)";

      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template
          .update(sqlQuery, new Object[] {topicId, topicId, currentTimestamp, topicId,
              currentTimestamp, topicId, currentTimestamp, topicId, dayBefore, currentTimestamp,
              limit});
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<Integer> loadTopicIdsWithCountsMoreThan(int maxPopularCount, TABLE_NAME table) {
    EtmPoint perfPoint = getPerformancePoint(".loadTopicIdsWithCountsMoreThan()");
    try {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.query("select count(id) as max, topic_id from " + table.name()
          + " group by topic_id having max > ?", new Object[] {maxPopularCount},
          new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
              return rs.getInt("topic_id");
            }
          });
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int deleteTweetByMaxCount(int topicId, int maxPopularCount, TABLE_NAME table) {
    EtmPoint perfPoint = getPerformancePoint(".loadTopicIdsWhereToManyPopularTweets()");
    try {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("tid", topicId);
      map.put("max_size", maxPopularCount);
      NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
      return template
          .update(
              DELETE_FROM_SUB_QUERY
                  + table.toString()
                  + " where id not in ( select id from (select id from "
                  + table.toString()
                  + " where topic_id = :tid order by retweets_count desc limit :max_size) as etalon) and topic_id= :tid",
              map);
    } finally {
      perfPoint.collect();
    }
  }
}
