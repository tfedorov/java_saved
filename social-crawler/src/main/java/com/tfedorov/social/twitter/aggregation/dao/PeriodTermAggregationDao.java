package com.tfedorov.social.twitter.aggregation.dao;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.base.BaseDateTime;

import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate.AGGREGATE_TYPE_MAPPING;

public interface PeriodTermAggregationDao {

  static final String FAKE_TERM = "#(<font color=\"green\">agg-sts</font>)#";

  /**
   * 
   * @param topicId
   * @param edate
   * @param period
   * @param type
   * @return
   */
  List<PeriodTermAggregate> getTermsByPeriod(BigInteger topicId, BaseDateTime edate, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type, int limit);

  /**
   * 
   * @param topicId
   * @param edate
   * @param type
   * @return
   */
  List<PeriodTermAggregate> getRawTerms(BigInteger topicId, BaseDateTime edate,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type, int limit);

  List<PeriodTermSoOccurrencyAggregate> selectRawBiTermsSoOccurency(BigInteger topicId,
      int limitTerms, int limitPairs, BaseDateTime date, int termsCount);

  List<PeriodTermSoOccurrencyAggregate> selectPeriodBiTermsSoOccurency(BigInteger topicId,
      int limitTerms, int limitPairs, BaseDateTime date, int period);

  int aggregateTermsFromRawData(BigInteger topicId, BaseDateTime eDate, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type);

  int aggregateTermsFromDaysPeriods(BigInteger topicId, BaseDateTime eDate, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type);

  List<PeriodTermAggregate> getAggregationStats(final BigInteger topicId, final BaseDateTime date,
      final int period, final PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type, int limit);

  @Deprecated
  int cleanupAllPeriodAgregation(BigInteger topicId);

  int cleanupPeriodTerms(BigInteger topicId, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type,
      String keyWord);

  int cleanupRawAgregationForTopic(BigInteger topicId, BaseDateTime eDate,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type);

  int cleanupPeriodAgregationForTopic(BigInteger topicId, BaseDateTime eDate, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type);

  int cleanupAgregationByDates(BaseDateTime upTo);

  @Deprecated
  List<TopicTweetAggregate> selectLatestTweetsFromRawTable(BigInteger topicId, String term,
      BaseDateTime date, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type);
  @Deprecated
  List<TopicTweetAggregate> selectLatestTweetsFromPeriodTable(BigInteger topicId, String term,
      BaseDateTime date, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type, int period);

  TopicTermAggregate selectLatestTweetIdsFromRawTable(BigInteger topicId, String term,
      BaseDateTime date, AGGREGATE_TYPE_MAPPING type);

  TopicTermAggregate selectLatestTweetIdsFromPeriodTable(BigInteger topicId, String term,
      BaseDateTime date, AGGREGATE_TYPE_MAPPING type, int period);
}
