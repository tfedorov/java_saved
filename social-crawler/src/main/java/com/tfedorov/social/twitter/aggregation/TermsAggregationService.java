package com.tfedorov.social.twitter.aggregation;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermSoOccurrencyAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;

public interface TermsAggregationService {

  void aggregateAllTopicsTermsForToday();

  // public void aggregateAllTopicTermsFor30Days();

  // aggregateTermsForTopicDate and aggregateTermsPart used for debugger purposes only
  /*
   * public int aggregateTermsForTopicDate (BigInteger topicId, DateTime date);
   * 
   * public int aggregateTermsPart (BigInteger topicId, DateTime date, int period,
   * PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type);
   */

  List<PeriodTermSoOccurrencyAggregate> selectSoOccurrencyAggregate(BigInteger topicId,
                                                                    DateMidnight dateTime, int period, int limitTerms, int limitPairs);

  /**
   * @param topicId
   * @param date
   * @param period
   * @param type
   * @return
   * @throws IOException
   * @throws JSONException
   */
  JSONObject selectTermsAggregates(BigInteger topicId, int period, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type,
      int limit) throws IOException, JSONException;


  List<PeriodTermAggregate> getAggregationStatsPart(BigInteger topicId, DateTime date, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type);

  List<PeriodTermAggregate> getAggregationStatsForTopicDate(BigInteger topicId, DateTime date);

  boolean isTermAgregationRunning();

  JsonNode getClusterData(BigInteger topicId, int limit, int clusterCount, DateMidnight dateTime,
      int daysRange, String algorithm, int relative) throws JsonGenerationException,
      JsonMappingException, IOException;

  JsonNode getClusterAlgorithms();

  @Deprecated
  List<TopicTweetAggregate> getLatestTweets(BigInteger topicId, String term, DateMidnight date,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING aggregateTypeMapping);

  @Deprecated
  List<TopicTweetAggregate> getLatestTweetsByRange(BigInteger topicId, String term, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING aggregateTypeMapping);

  void aggregateTerms();

  TopicTermAggregate getLatestTweetIds(BigInteger topicId, String term, DateMidnight date,
                                       PeriodTermAggregate.AGGREGATE_TYPE_MAPPING aggregateTypeMapping);

  TopicTermAggregate getLatestTweetIdsByRange(BigInteger topicId, String term, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING aggregateTypeMapping);

}
