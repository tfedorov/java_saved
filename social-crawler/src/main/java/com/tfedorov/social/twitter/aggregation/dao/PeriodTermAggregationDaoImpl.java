package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import com.tfedorov.social.utils.date.DateUtils;
import org.joda.time.base.BaseDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.tfedorov.social.twitter.aggregation.DaoException;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate.AGGREGATE_TYPE_MAPPING;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Repository("periodTermAggregationDao")
public class PeriodTermAggregationDaoImpl implements PeriodTermAggregationDao {

  private static final Logger logger = LoggerFactory.getLogger(PeriodTermAggregationDaoImpl.class);

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  @Autowired
  private DataSource dataSource;

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(PeriodTermAggregationDaoImpl.class
        .toString()).append(name).toString());
  }

  /**
   * Reads data from preaggregated tables - topic_terms_p, topic_bi_terms_p, topic_tri_terms_p
   */
  @Override
  public List<PeriodTermAggregate> getTermsByPeriod(final BigInteger topicId,
      final BaseDateTime date, final int period,
      final PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type, int limit) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".getTermsByPeriod():").append(type.name())
            .append(":").append(period).toString());
    try {
      String sqlQuery =
          "select topic_id, term, adatetime, terms_count, period from "
              + type.name()
              + " where topic_id = ? and adatetime = ? and period = ? and terms_count > 0 order by terms_count desc";

      JdbcTemplate template = new JdbcTemplate(dataSource);

      if (limit > 0) {
        template.setMaxRows(limit);
      }

      return template.query(sqlQuery, new Object[] {topicId, DateUtils.convertToTimeStamp(date),
          period}, new RowMapper<PeriodTermAggregate>() {

        @Override
        public PeriodTermAggregate mapRow(ResultSet rs, int i) throws SQLException {
          PeriodTermAggregate tta =
              new PeriodTermAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
                  .getString("term"), DateUtils.convertToDateTime(rs.getTimestamp("adatetime")), rs
                  .getInt("period"), rs.getLong("terms_count"), type);
          return tta;
        }

      });
    } finally {
      perfPoint.collect();
    }
  }

  /**
   * Reads data from raw data tables - topic_terms, topic_bi_terms, topic_tri_terms
   */
  @Override
  public List<PeriodTermAggregate> getRawTerms(BigInteger topicId, BaseDateTime date,
      final AGGREGATE_TYPE_MAPPING type, int limit) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".getRawTerms():").append(type.getRawTable())
            .toString());
    try {
      String sqlQuery =
          "select topic_id, term, adatetime, terms_count from " + type.getRawTable()
              + " where topic_id = ? and adatetime = ? order by terms_count desc";

      JdbcTemplate template = new JdbcTemplate(dataSource);

      if (limit > 0) {
        template.setMaxRows(limit);
      }

      return template.query(sqlQuery, new Object[] {topicId, DateUtils.convertToTimeStamp(date)},
          new RowMapper<PeriodTermAggregate>() {

            @Override
            public PeriodTermAggregate mapRow(ResultSet rs, int i) throws SQLException {
              PeriodTermAggregate tta =
                  new PeriodTermAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
                      .getString("term"),
                      DateUtils.convertToDateTime(rs.getTimestamp("adatetime")), 1, rs
                          .getLong("terms_count"), type);
              return tta;
            }

          });
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<PeriodTermSoOccurrencyAggregate> selectRawBiTermsSoOccurency(BigInteger topicId,
      int limitTerms, int limitPairs, BaseDateTime date, int termsCount) {

    EtmPoint perfPoint = getPerformancePoint(".selectRawBiTermsSoOccurency()");
    try {
      String sqlQuery =
          " select id, biterms.topic_id, term1, singleterm1.terms_count as term1_count, term2, singleterm2.terms_count as term2_count, biterms.term, biterms.adatetime, biterms.terms_count "
              + " FROM "
              + " (SELECT id, topic_id, SUBSTRING_INDEX(term, ' ',1) as term1, SUBSTRING_INDEX(term, ' ',-1) as term2, term, terms_count, adatetime "
              + "  FROM topic_bi_terms where topic_id = ? and adatetime = ? and terms_count > ? order by terms_count desc limit ?) as biterms "
              + " JOIN "
              + " (SELECT term, terms_count FROM topic_terms where topic_id = ? and adatetime = ?  order by terms_count desc limit ?) singleterm1 "
              + " ON (biterms.term1 = singleterm1.term) "
              + " JOIN "
              + " (SELECT term, terms_count FROM topic_terms where topic_id = ? and adatetime = ?  order by terms_count desc limit ?) singleterm2 "
              + " ON (biterms.term2 = singleterm2.term)";


      final JdbcTemplate template = new JdbcTemplate(dataSource);
      Timestamp dateTime = DateUtils.convertToTimeStamp(date);
      return template.query(sqlQuery, new Object[] {topicId, dateTime, termsCount, limitPairs,
          topicId, dateTime, limitTerms, topicId, dateTime, limitTerms},
          new RowMapper<PeriodTermSoOccurrencyAggregate>() {

            public PeriodTermSoOccurrencyAggregate mapRow(ResultSet rs, int i) throws SQLException {
              PeriodTermSoOccurrencyAggregate tta =
                  new PeriodTermSoOccurrencyAggregate(rs.getBigDecimal("topic_id").toBigInteger(),
                      rs.getString("term1"), rs.getLong("term1_count"), rs.getString("term2"), rs
                          .getLong("term2_count"), rs.getLong("terms_count"), DateUtils
                          .convertToDateTime(rs.getTimestamp("adatetime")), 1);
              return tta;

            }

          });



    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<PeriodTermSoOccurrencyAggregate> selectPeriodBiTermsSoOccurency(BigInteger topicId,
      int limitTerms, int limitPairs, BaseDateTime date, final int period) {

    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".selectPeriodBiTermsSoOccurency():").append(":")
            .append(period).toString());

    try {
      String sqlQuery =
          " select id, biterms.topic_id, term1, singleterm1.terms_count as term1_count, term2, singleterm2.terms_count as term2_count, biterms.term, biterms.adatetime, biterms.terms_count "
              + " FROM "
              + " (SELECT id, topic_id, SUBSTRING_INDEX(term, ' ',1) as term1, SUBSTRING_INDEX(term, ' ',-1) as term2, term, terms_count, adatetime "
              + "  FROM topic_bi_terms_p where topic_id = ? and adatetime = ? and terms_count > 1 and period = ? order by terms_count desc limit ?) as biterms "
              + " JOIN "
              + " (SELECT term, terms_count FROM topic_terms_p where topic_id = ? and adatetime = ?  and period = ? and terms_count > 0 order by terms_count desc limit ?) singleterm1 "
              + " ON (biterms.term1 = singleterm1.term) "
              + " JOIN "
              + " (SELECT term, terms_count FROM topic_terms_p where topic_id = ? and adatetime = ?  and period = ? and terms_count > 0 order by terms_count desc limit ?) singleterm2 "
              + " ON (biterms.term2 = singleterm2.term)";


      final JdbcTemplate template = new JdbcTemplate(dataSource);
      Timestamp dateTime = DateUtils.convertToTimeStamp(date);
      return template.query(sqlQuery, new Object[] {topicId, dateTime, period, limitPairs, topicId,
          dateTime, period, limitTerms, topicId, dateTime, period, limitTerms},
          new RowMapper<PeriodTermSoOccurrencyAggregate>() {

            public PeriodTermSoOccurrencyAggregate mapRow(ResultSet rs, int i) throws SQLException {
              PeriodTermSoOccurrencyAggregate tta =
                  new PeriodTermSoOccurrencyAggregate(rs.getBigDecimal("topic_id").toBigInteger(),
                      rs.getString("term1"), rs.getLong("term1_count"), rs.getString("term2"), rs
                          .getLong("term2_count"), rs.getLong("terms_count"), DateUtils
                          .convertToDateTime(rs.getTimestamp("adatetime")), period);
              return tta;

            }

          });



    } finally {
      perfPoint.collect();
    }
  }

  /**
   * Get FAKE_TERM records to present aggregation statistics
   */
  @Override
  public List<PeriodTermAggregate> getAggregationStats(final BigInteger topicId,
      final BaseDateTime date, final int period,
      final PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type, int limit) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".getAggregationStats():").append(type.name())
            .append(":").append(period).toString());
    try {
      String sqlQuery =
          "select topic_id, term, adatetime, terms_count, period from " + type.name()
              + " where topic_id = ? and adatetime <= ? and period = ? and terms_count = 0 "
              + " and term = ? order by adatetime desc";

      JdbcTemplate template = new JdbcTemplate(dataSource);

      if (limit > 0) {
        template.setMaxRows(limit);
      }

      return template.query(sqlQuery, new Object[] {topicId, DateUtils.convertToTimeStamp(date),
          period, FAKE_TERM}, new RowMapper<PeriodTermAggregate>() {

        @Override
        public PeriodTermAggregate mapRow(ResultSet rs, int i) throws SQLException {
          PeriodTermAggregate tta =
              new PeriodTermAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
                  .getString("term"), DateUtils.convertToDateTime(rs.getTimestamp("adatetime")), rs
                  .getInt("period"), rs.getLong("terms_count"), type);
          return tta;
        }

      });
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int aggregateTermsFromRawData(BigInteger topicId, BaseDateTime eDate, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type) {


    EtmPoint perfPoint =
        getPerformancePoint(".aggregateTermsFromRawData():" + type.name() + ":" + period);

    try {

      if (period != 1) {
        throw new IllegalArgumentException("period=" + period
            + " aggregation based on raw data isn't allowed - only on days preaggregation");
      }
      int limit = type.getLimit();


      String sqlQuery =
          new StringBuilder("insert into ")
              .append(type.name())
              .append(
                  " (topic_id, term, adatetime, period, terms_count, modification_date, tw1_id, tw2_id, tw3_id ) ")
              .append(
                  " ( select topic_id, term, ?, ?, sum(terms_count), CURRENT_TIMESTAMP, max(tw1_id), max(tw2_id), max(tw3_id) from ")
              .append(type.getRawTable())
              .append(
                  " where topic_id = ? and adatetime >= DATE_SUB(?, INTERVAL ? DAY) and adatetime < ? group by topic_id, term order by sum(terms_count) desc limit ")
              .append(limit).append(" )")
              .append(" union  ( select ?, ?, ?, ?, 0, CURRENT_TIMESTAMP,0,0,0 )").toString();

      JdbcTemplate template = new JdbcTemplate(dataSource);

      Timestamp date = DateUtils.convertToTimeStamp(eDate);

      return template.update(sqlQuery, new Object[] {date, period, topicId, date, period, date,
          topicId, FAKE_TERM, date, period});
    } catch (DataAccessException e) {
      String msg =
          new StringBuilder("topicId =").append(topicId).append(", eDate = ").append(eDate)
              .append(" , period = ").append(period).append(", type = ").append(type).toString();
      throw new DaoException(msg, e);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int aggregateTermsFromDaysPeriods(BigInteger topicId, BaseDateTime eDate, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type) {

    EtmPoint perfPoint =
        getPerformancePoint(".aggregateTermsFromDaysPeriods():" + type.name() + ":" + period);

    try {
      if (period == 1) {
        throw new IllegalArgumentException(
            "period=1 aggregation based on days preaggregation isn't allowed - will cause duplication");
      }
      int limit = type.getLimit();

      String sqlQuery =
          new StringBuilder("insert into ")
              .append(type.name())
              .append(
                  " (topic_id, term, adatetime, period, terms_count, modification_date, tw1_id, tw2_id, tw3_id ) ")
              .append(
                  " ( select topic_id, term, ?, ?, sum(terms_count), CURRENT_TIMESTAMP, max(tw1_id), max(tw2_id), max(tw3_id) from ")
              .append(type.name())
              .append(
                  " where topic_id = ? and adatetime > DATE_SUB(?, INTERVAL ? DAY) and adatetime <= ? and period = 1 and terms_count > 0 group by topic_id, term order by sum(terms_count) desc limit ")
              .append(limit).append(" ) ")
              .append(" union  ( select ?, ?, ?, ?, 0, CURRENT_TIMESTAMP,0,0,0 )").toString();

      JdbcTemplate template = new JdbcTemplate(dataSource);

      return template.update(
          sqlQuery,
          new Object[] {DateUtils.convertToTimeStamp(eDate), period, topicId,
              DateUtils.convertToTimeStamp(eDate), period, DateUtils.convertToTimeStamp(eDate),
              topicId, FAKE_TERM, DateUtils.convertToTimeStamp(eDate), period});
    } catch (DataAccessException e) {
      String msg =
          new StringBuilder("topicId =").append(topicId).append(", eDate = ").append(eDate)
              .append(" , period = ").append(period).append(", type = ").append(type).toString();
      throw new DaoException(msg, e);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public int cleanupAllPeriodAgregation(BigInteger topicId) {
    int res = 0;
    EtmPoint perfPoint = getPerformancePoint(".cleanupAll()");
    try {
      for (PeriodTermAggregate.AGGREGATE_TYPE_MAPPING name : PeriodTermAggregate.AGGREGATE_TYPE_MAPPING
          .values()) {
        res = +deleteRowsFromTableByTopicId(topicId, name.name());
      }
      return res;
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public int cleanupPeriodTerms(BigInteger topicId,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type, String keyWord) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".cleanupTerms(keyWord):").append(type.name())
            .toString());
    try {
      String sqlQuery =
          new StringBuilder("delete from ").append(type.name()).append(" where topic_id = ? ")
              .append(" AND term = ?").toString();
      JdbcTemplate template = new JdbcTemplate(dataSource);
      return template.update(sqlQuery, new Object[] {topicId, keyWord});
    } catch (DataAccessException e) {
      String msg =
          new StringBuilder("topicId =").append(topicId).append(", type = ").append(type)
              .append(" , keyWord = ").append(keyWord).toString();
      throw new DaoException(msg, e);
    } finally {
      perfPoint.collect();
    }
  }

  private int deleteRowsFromTableByTopicId(BigInteger topicId, String tableName) {
    int res = 0;
    EtmPoint perfPoint = getPerformancePoint(".delete():" + tableName);
    try {
      String sqlQuery = "delete from " + tableName + " where topic_id=? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      res = +template.update(sqlQuery, new Object[] {topicId});
      return res;
    } catch (DataAccessException e) {
      String msg =
          new StringBuilder("topicId =").append(topicId).append(", tableName = ").append(tableName)
              .toString();
      throw new DaoException(msg, e);
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public int cleanupRawAgregationForTopic(BigInteger topicId, BaseDateTime eDate,
      AGGREGATE_TYPE_MAPPING type) {
    EtmPoint perfPoint =
        getPerformancePoint(".cleanupRawAgregationForTopic():" + type.getRawTable());

    int res = 0;

    try {

      String sqlQuery =
          "delete from " + type.getRawTable() + " where topic_id = ? and adatetime < ? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      res = +template.update(sqlQuery, new Object[] {topicId, DateUtils.convertToTimeStamp(eDate)});

      logger.debug("Cleaned up " + res + " rows from " + type.getRawTable());
      return res;
    } catch (DataAccessException e) {
      String msg =
          new StringBuilder("topicId =").append(topicId).append(", eDate = ").append(eDate)
              .append(", type = ").append(type).toString();
      throw new DaoException(msg, e);
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public int cleanupPeriodAgregationForTopic(BigInteger topicId, BaseDateTime eDate, int period,
      AGGREGATE_TYPE_MAPPING type) {
    EtmPoint perfPoint =
        getPerformancePoint(".cleanupPeriodAgregationForTopic():" + type.name() + ":" + period);

    int res = 0;

    try {

      if (period == 1) {
        throw new IllegalArgumentException(
            "period=1 cleanup days based preaggregation isn't allowed");
      }
      String sqlQuery =
          "delete from " + type.name() + " where topic_id = ? and period = ? and adatetime < ? ";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      res =
          +template.update(sqlQuery,
              new Object[] {topicId, period, DateUtils.convertToTimeStamp(eDate)});
      return res;
    } catch (DataAccessException e) {
      String msg =
          new StringBuilder("topicId =").append(topicId).append(", eDate = ").append(eDate)
              .append(", period = ").append(period).append(", type = ").append(type).toString();
      throw new DaoException(msg, e);
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public int cleanupAgregationByDates(BaseDateTime upTo) {
    int res = 0;
    EtmPoint perfPoint = getPerformancePoint(".cleanupAgregationByDates()");
    try {
      for (PeriodTermAggregate.AGGREGATE_TYPE_MAPPING name : PeriodTermAggregate.AGGREGATE_TYPE_MAPPING
          .values()) {
        res += deleteRowsFromTableByDate(name, upTo);
      }
      return res;
    } finally {
      perfPoint.collect();
    }
  }

  private int deleteRowsFromTableByDate(PeriodTermAggregate.AGGREGATE_TYPE_MAPPING name,
      BaseDateTime upTo) {
    EtmPoint perfPoint =
        getPerformancePoint(new StringBuilder(".deleteRowsFromTableByTopicsDate():").append(name)
            .toString());
    int res = 0;
    try {
      String sqlQuery = "delete from " + name + " where adatetime < ?";
      JdbcTemplate template = new JdbcTemplate(dataSource);
      res += template.update(sqlQuery, new Object[] {DateUtils.convertToTimeStamp(upTo)});
      logger.debug("RUN QUERY: "
          + sqlQuery.replace("?", DateUtils.convertToTimeStamp(upTo).toString()));
      // System.out.println("RUN QUERY: " + sqlQuery.replace("?",
      // upTo.toString()));
    } catch (DataAccessException e) {
      String msg =
          new StringBuilder("name =").append(name).append(", upTo = ").append(upTo).toString();
      throw new DaoException(msg, e);
    } finally {
      perfPoint.collect();
    }
    return res;
  }

  @Deprecated
  @Override
  public List<TopicTweetAggregate> selectLatestTweetsFromRawTable(BigInteger topicId, String term,
      BaseDateTime date, AGGREGATE_TYPE_MAPPING type) {

    EtmPoint perfPoint = getPerformancePoint(".selectLatestTweetsFromRawTable()");
    try {
      String sqlQuery =
          "select topic_id, tweet_id, text, from_user_id, from_user, profile_image_url, profile_image_url_https, created_at from latest_tweets "
              + "join (select tw1_id, tw2_id, tw3_id from "
              + type.getRawTable()
              + " where topic_id = ? and term = ? and adatetime = ?) as term "
              + "on (term.tw1_id = latest_tweets.tweet_id and latest_tweets.topic_id = ?) "
              + "or (term.tw2_id = latest_tweets.tweet_id and latest_tweets.topic_id = ?) "
              + "or (term.tw3_id = latest_tweets.tweet_id and latest_tweets.topic_id = ?)";

      final JdbcTemplate template = new JdbcTemplate(dataSource);
      Timestamp dateTime = DateUtils.convertToTimeStamp(date);
      return template.query(sqlQuery, new Object[] {topicId, term, dateTime, topicId, topicId,
          topicId}, new RowMapper<TopicTweetAggregate>() {
        public TopicTweetAggregate mapRow(ResultSet rs, int i) throws SQLException {
          TopicTweetAggregate tta =
              new TopicTweetAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
                  .getBigDecimal("tweet_id").toBigInteger(), rs.getString("text"), rs
                  .getBigDecimal("from_user_id").toBigInteger(), rs.getString("from_user"), rs
                  .getString("profile_image_url"), rs.getString("profile_image_url_https"),
                  DateUtils.convertToDateTime(rs.getTimestamp("created_at")));
          return tta;
        }
      });
    } finally {
      perfPoint.collect();
    }
  }

  @Deprecated
  @Override
  public List<TopicTweetAggregate> selectLatestTweetsFromPeriodTable(BigInteger topicId,
      String term, BaseDateTime date, AGGREGATE_TYPE_MAPPING type, int period) {
    EtmPoint perfPoint = getPerformancePoint(".selectLatestTweetsFromPeriodTable()");

    try {
      String sqlQuery =
          "select topic_id, tweet_id, text, from_user_id, from_user, profile_image_url, profile_image_url_https, created_at from latest_tweets "
              + "join (select tw1_id, tw2_id, tw3_id from "
              + type.name()
              + " where topic_id = ? and term = ? and adatetime = ? and period = ?) as term "
              + "on (term.tw1_id = latest_tweets.tweet_id and latest_tweets.topic_id = ?) "
              + "or (term.tw2_id = latest_tweets.tweet_id and latest_tweets.topic_id = ?) "
              + "or (term.tw3_id = latest_tweets.tweet_id  and latest_tweets.topic_id = ?)";

      final JdbcTemplate template = new JdbcTemplate(dataSource);
      Timestamp dateTime = DateUtils.convertToTimeStamp(date);
      return template.query(sqlQuery, new Object[] {topicId, term, dateTime, period, topicId,
          topicId, topicId}, new RowMapper<TopicTweetAggregate>() {
        public TopicTweetAggregate mapRow(ResultSet rs, int i) throws SQLException {
          TopicTweetAggregate tta =
              new TopicTweetAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
                  .getBigDecimal("tweet_id").toBigInteger(), rs.getString("text"), rs
                  .getBigDecimal("from_user_id").toBigInteger(), rs.getString("from_user"), rs
                  .getString("profile_image_url"), rs.getString("profile_image_url_https"),
                  DateUtils.convertToDateTime(rs.getTimestamp("created_at")));
          return tta;
        }
      });
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public TopicTermAggregate selectLatestTweetIdsFromRawTable(BigInteger topicId, String term,
      BaseDateTime date, AGGREGATE_TYPE_MAPPING type) {

    EtmPoint perfPoint = getPerformancePoint(".selectLatestTweetsFromRawTable()");
    try {
      String sqlQuery =
          "select topic_id, term, tw1_id, tw2_id, tw3_id from " + type.getRawTable()
              + " where topic_id = ? and term = ? and adatetime = ?";

      final JdbcTemplate template = new JdbcTemplate(dataSource);
      Timestamp dateTime = DateUtils.convertToTimeStamp(date);
      return template.queryForObject(sqlQuery, new RowMapper<TopicTermAggregate>() {
        public TopicTermAggregate mapRow(ResultSet rs, int i) throws SQLException {
          return new TopicTermAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
              .getString("term"), rs.getBigDecimal("tw1_id"), rs
              .getBigDecimal("tw2_id"), rs.getBigDecimal("tw3_id"));
        }
      }, topicId, term, dateTime);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public TopicTermAggregate selectLatestTweetIdsFromPeriodTable(BigInteger topicId, String term,
      BaseDateTime date, AGGREGATE_TYPE_MAPPING type, int period) {
    EtmPoint perfPoint = getPerformancePoint(".selectLatestTweetsFromPeriodTable()");

    try {
      String sqlQuery =
          "select topic_id, term, tw1_id, tw2_id, tw3_id from " + type.name()
              + " where topic_id = ? and term = ? and adatetime = ? and period = ?";

      final JdbcTemplate template = new JdbcTemplate(dataSource);
      Timestamp dateTime = DateUtils.convertToTimeStamp(date);
      return template.queryForObject(sqlQuery, new RowMapper<TopicTermAggregate>() {
        public TopicTermAggregate mapRow(ResultSet rs, int i) throws SQLException {
          return new TopicTermAggregate(rs.getBigDecimal("topic_id").toBigInteger(), rs
              .getString("term"), rs.getBigDecimal("tw1_id"), rs
              .getBigDecimal("tw2_id"), rs.getBigDecimal("tw3_id"));
        }
      }, topicId, term, dateTime, period);
    } finally {
      perfPoint.collect();
    }
  }
}
