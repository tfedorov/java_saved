package com.tfedorov.social.twitter.aggregation;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.tfedorov.social.clustering.Cluster;
import com.tfedorov.social.clustering.ClusteringAlgorithmsFactory;
import com.tfedorov.social.clustering.ClustersCalculator;
import com.tfedorov.social.clustering.CoOccurrenceInfo;
import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.dao.TopicDao;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregationDao;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermSoOccurrencyAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import com.tfedorov.social.utils.JsonUtils;
import com.tfedorov.social.utils.date.DateUtils;
import com.tfedorov.social.utils.date.TimeZoneConstants;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfedorov.social.topic.TopicType;
import com.tfedorov.social.twitter.aggregation.dao.TopicTweetAggregate;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("termsAggregationService")
public class TermsAggregationServiceImpl implements TermsAggregationService {


  private static final String WEIGHT = "weight";

  private static final String STEMMING_MAP = "stemmingMap";

  private static final String JSON_CLUSTERS = "clusters";

  private static final int TERMS_COUNT_ADVANCED = 0;

  private static final int TERMS_COUNT_DEFAULT = 1;

  private static final String ALGORITHMS = "algorithms";

  private static final String JSON_KEY = "key";

  private static final String SIZE = "size";

  private static final String CHILDREN = "children";

  private static final String JSON_NAME = "name";

  private static final String SPACE = " ";

  private final Logger logger = LoggerFactory.getLogger(TermsAggregationServiceImpl.class);

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private AtomicBoolean termAgregationLaunching = new AtomicBoolean(false);

  @Autowired
  private TopicDao topicDao;


  @Autowired
  private PeriodTermAggregationDao periodTermAggregationDao;

  @Autowired
  private StemmingService stemmingService;


  public TermsAggregationServiceImpl() {}


  @Override
  public boolean isTermAgregationRunning() {
    return termAgregationLaunching.get();
  }

  @Override
  public JsonNode getClusterAlgorithms() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode node = mapper.createObjectNode();
    final ArrayNode arrayNode = mapper.createArrayNode();
    for (ClusteringAlgorithmsFactory.ClusteringAlgorithm algorithm : ClusteringAlgorithmsFactory.ClusteringAlgorithm.values()) {
      final ObjectNode obj = mapper.createObjectNode();
      obj.put(JSON_KEY, algorithm.getKey());
      obj.put(JSON_NAME, algorithm.getDisplayName());
      arrayNode.add(obj);
    }
    node.put(ALGORITHMS, arrayNode);
    return node;
  }

  @Override
  public JsonNode getClusterData(BigInteger topicId, int limit, int clusterCount,
      DateMidnight dateTime, int daysRange, String algorithm, int weightAlgorithm)
      throws JsonGenerationException, JsonMappingException, IOException {

    EtmPoint perfPoint = getPerformancePoint(".getClusterData()");

    try {
      int limitTerms = limit;
      int limitPairs = limit * (limit - 1) / 2; // n*(n-1)/2

      List<PeriodTermSoOccurrencyAggregate> biTerms =
          selectSoOccurrencyAggregate(topicId, dateTime, daysRange, limitTerms, limitPairs);

      // gather terms counts
      final List<String> terms = new ArrayList<String>();
      final Map<String, Long> wordCount = new HashMap<String, Long>();
      for (PeriodTermSoOccurrencyAggregate top : biTerms) {
        wordCount.put(top.getFirstTerm(), top.getFirtsTermCount());
        wordCount.put(top.getSecondTerm(), top.getSecondTermCount());
        terms.add(top.getFirstTerm());
        terms.add(top.getSecondTerm());
      }

      final List<CoOccurrenceInfo> wordsPairs = new ArrayList<CoOccurrenceInfo>(biTerms.size());
      for (PeriodTermAggregate term : biTerms) {
        final String[] names = term.getTerm().split(SPACE);
        if (names.length > 1) {
          long weight = term.getTermsCount();
          if (weightAlgorithm == 1) {
            Long firstCount = wordCount.get(names[0]);
            Long secondCount = wordCount.get(names[1]);
            weight = (long) (((double) (weight * weight / firstCount * secondCount)) * 100);
          }
          wordsPairs.add(new CoOccurrenceInfo(names[0], names[1], weight));
        }
      }

      final ClustersCalculator clusterCalculator =
          ClusteringAlgorithmsFactory.getAlgorithm(algorithm);
      final List<Cluster> clusters = clusterCalculator.calculate(clusterCount, wordsPairs);

      ObjectNode objectNode =
          generateCLusterJSON(clusters, wordCount, wordsPairs,
              stemmingService.loadTopWordsListByStemmed(terms, StemmingService.DEFAULT_LANGUAGE));

      return objectNode;
    } finally {
      perfPoint.collect();
    }
  }


  @Override
  public List<PeriodTermSoOccurrencyAggregate> selectSoOccurrencyAggregate(BigInteger topicId,
      DateMidnight dateTime, int period, int limitTerms, int limitPairs) {
    EtmPoint perfPoint = getPerformancePoint(".selectSoOccurrencyAggregate()");

    try {
      if ((dateTime == null && period == 1)
          || (dateTime != null && dateTime.isEqual(DateUtils.getCurrentMidnight().getMillis()))) {
        if (dateTime == null) {
          dateTime = DateUtils.getCurrentMidnight();
        }
        List<PeriodTermSoOccurrencyAggregate> biTerms =
            periodTermAggregationDao.selectRawBiTermsSoOccurency(topicId, limitTerms, limitPairs,
                dateTime, TERMS_COUNT_DEFAULT);

        if (biTerms != null && biTerms.isEmpty()) {
          biTerms =
              periodTermAggregationDao.selectRawBiTermsSoOccurency(topicId, limitTerms, limitPairs,
                  dateTime, TERMS_COUNT_ADVANCED);
        }
        return biTerms;
      } else {
        if (dateTime != null) {
          dateTime = dateTime.plusDays(1);
          period = 1;
        } else {
          dateTime = DateUtils.getCurrentMidnight();
        }
        return periodTermAggregationDao.selectPeriodBiTermsSoOccurency(topicId, limitTerms,
            limitPairs, dateTime, period);
      }
    } finally {
      perfPoint.collect();
    }

  }

  /**
   * @param topicId
   * @param period
   * @param type
   * @return
   * @throws JSONException
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonGenerationException
   */
  @Override
  public JSONObject selectTermsAggregates(BigInteger topicId, int period,
                                          PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type, int limit) throws IOException, JSONException {
    EtmPoint perfPoint = getPerformancePoint(".selectTermsAggregates():" + period);

    try {
      List<PeriodTermAggregate> terms = null;
      if (period == 1) {
        // get from raw data - realtime for today
        terms =
            periodTermAggregationDao.getRawTerms(topicId, DateUtils.getCurrentMidnight(), type,
                limit);

      } else {
        // get from preaggregated data - today
        terms =
            periodTermAggregationDao.getTermsByPeriod(topicId, DateUtils.getCurrentMidnight(),
                period, type, limit);
      }
      final Map<String, String> mapSteaming =
          stemmingService.loadTopWordsListByStemmed(convertToList(terms),
              StemmingService.DEFAULT_LANGUAGE);

      JSONObject jsonObject = JsonUtils.wrapTableResponse(terms);
      jsonObject.put(STEMMING_MAP, mapSteaming);

      return jsonObject;

    } finally {
      perfPoint.collect();
    }
  }

  @Deprecated
  protected void aggregateAllTopicTermsFor30Days() {
    EtmPoint perfPoint = getPerformancePoint(".aggregateAllTopicTermsFor30Days()");


    try {

      List<Topic> topics = topicDao.getTrackedTopics();
      logger.warn("Starting terms preaggregation for 30 days !!! for " + topics.size() + " topics");

      int days = 30;

      DateTime today = new DateMidnight(TimeZoneConstants.TIME_ZONE).toDateTime();

      DateTime past30 = today.minusDays(days);

      int rows = 0;

      for (int i = 1; i <= days; i++) {

        DateTime date = past30.plusDays(i);

        logger.warn("Starting terms preaggregation for 30 days period, for date: "
            + date.toString("yyyy-MM-dd"));

        for (Topic topic : topics) {


          try {
            rows += aggregateTermsForTopicDate(topic, date.toDateMidnight());
          } catch (Exception e) {
            logger.error("Error within preaggregation for topicId=" + topic.getId() + " for date "
                + date, e);
          }
        }

        logger.warn("Finished terms preaggregation for 30 days period, for date: "
            + date.toString("yyyy-MM-dd"));

      }

      logger.warn("Finished terms preaggregation: " + rows + " rows 30 days !!! for "
          + topics.size() + " topics");

    } finally {
      perfPoint.collect();
    }

  }


  @Override
  public void aggregateAllTopicsTermsForToday() {
    EtmPoint perfPoint = getPerformancePoint(".aggregateAllTopicsTermsForToday()");

    if (!termAgregationLaunching.compareAndSet(false, true)) {
      // If termAgregationLaunching variable was true
      // Agregation was launchin already
      logger.warn("Aggregat in progress running by other thread!");
      return;
    }
    try {

      List<Topic> topics = topicDao.getTrackedTopics();
      logger.info("Starting terms preaggregation for today - " + topics.size() + " topics");

      DateTime today = DateUtils.getCurrentDateTime();

      int rows = 0;

      for (Topic topic : topics) {
        try {
          rows += aggregateTermsForTopicDate(topic, today.toDateMidnight());
        } catch (Exception e) {
          logger.error("Error within preaggregation for topicId=" + topic.getId(), e);
        }
      }

      logger.info("Finished terms preaggregation: " + rows + " rows for today - " + topics.size()
          + " topics");

    } finally {
      termAgregationLaunching.set(false);
      perfPoint.collect();
    }

  }

  protected int aggregateTermsForTopicDate(Topic topic, DateMidnight date) {

    EtmPoint perfPoint = getPerformancePoint(".aggregateTermsForTopicDate()");

    int rows = 0;

    try {

      BigInteger topicId = topic.getId();
      for (PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type : PeriodTermAggregate.AGGREGATE_TYPE_MAPPING
          .values()) {

        if (TopicType.CUSTOM == topic.getType()) {

          int period = 1;

          // will be aggregated from raw data (topic_terms, topic_bi_terms, topic_tri_terms)
          rows += aggregateTermsPart(topicId, date, period, type);
          period = 7;

          // will be aggregated from days data (topic_terms_p, topic_bi_terms_p, topic_tri_terms_p)
          rows += aggregateTermsPart(topicId, date, period, type);

          period = 30;

          // will be aggregated from days data (topic_terms_p, topic_bi_terms_p, topic_tri_terms_p)
          rows += aggregateTermsPart(topicId, date, period, type);

        } else {
          // For Industry & Trends Topic
          // Just clean old raw aggregated data
          int delResult =
              periodTermAggregationDao.cleanupRawAgregationForTopic(topicId, date, type);

          logger.info("Cleaned raw data " + delResult + " rows for topicId=" + topicId + " , date="
              + DateUtils.printDateTZ(date) + ", type=" + type.getRawTable());
        }

      }

      if (TopicType.CUSTOM == topic.getType()) {
        logger.info("For topicId=" + topicId + " preaggregated " + rows + " rows");
      }


      return rows;

    } finally {
      perfPoint.collect();
    }

  }

  protected int aggregateTermsPart(BigInteger topicId, DateMidnight lDate, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type) {
    EtmPoint perfPoint = getPerformancePoint(".aggregateTermsPart()");

    DateMidnight mnDate = lDate;
    try {

      List<PeriodTermAggregate> oldTA =
          periodTermAggregationDao.getAggregationStats(topicId, mnDate, period, type, 0);

      logger.debug("old size=" + oldTA.size() + " :" + oldTA.toString());

      // check if is not aggregated before
      if (oldTA.isEmpty() || oldTA.get(0).getDate().isBefore(mnDate)) {
        EtmPoint perfPoint1 = getPerformancePoint(".aggregateTermsPart():" + type.name());

        int result = 0;
        int delResult = 0;

        try {

          logger.info("Starting terms preaggregation for topicId=" + topicId);

          if (period == 1) {

            // by default last days pre aggregation was done previous day
            DateMidnight lastAggregatedDay = mnDate.minusDays(1);
            if (oldTA.isEmpty()) {
              // if there no day pre aggregations
              lastAggregatedDay = mnDate.minusDays(30);

              logger
                  .warn("There no days preaggregations at all - will preaggregated for past 30 days");
            } else {
              // get last day of preaggreagtion
              lastAggregatedDay = new DateMidnight(oldTA.get(0).getDate());

              if (lastAggregatedDay.plusDays(30).isBefore(mnDate)) {
                // if aggregations older than 30
                lastAggregatedDay = mnDate.minusDays(30);
              }
            }

            /*
             * result += periodTermAggregationDao.aggregateTermsFromRawData(topicId, new
             * Date(mnDate.getMillis()), period, type);
             * 
             * logger.info("Aggeragetd "+ result+" rows from RAW data for topicId=" + topicId +
             * " , date=" + new Date(mnDate.getMillis()) + ", period=" + period + ", type=" +
             * type.name() );
             * 
             * //clean old raw aggregated data delResult += periodTermAggregationDao
             * .cleanupRawAgregationForTopic(topicId, new Date(mnDate.getMillis()), type);
             * 
             * logger.info("Cleaned raw data " + delResult + " rows for topicId=" + topicId +
             * " , date=" + new Date(mnDate.getMillis()) + ", type=" + type.getRawTable());
             */

            if (lastAggregatedDay.plusDays(1).isBefore(mnDate)) {
              // notify about missed days
              logger
                  .warn("There missed days in days preaggregations - will preaggregated starting "
                      + lastAggregatedDay.plusDays(1));
            } else {
              logger.info("Will preaggregated dys starting " + lastAggregatedDay.plusDays(1));
            }


            while (lastAggregatedDay.isBefore(mnDate)) {

              lastAggregatedDay = lastAggregatedDay.plusDays(1);

              result +=
                  periodTermAggregationDao.aggregateTermsFromRawData(topicId, lastAggregatedDay,
                      period, type);

              logger.info("Aggeragetd " + result + " rows from RAW data for topicId=" + topicId
                  + " , date=" + DateUtils.printDateTZ(lastAggregatedDay) + ", period=" + period
                  + ", type=" + type.name());

              // clean old raw aggregated data
              delResult +=
                  periodTermAggregationDao.cleanupRawAgregationForTopic(topicId, lastAggregatedDay,
                      type);

              logger.info("Cleaned raw data " + delResult + " rows for topicId=" + topicId
                  + " , date=" + DateUtils.printDateTZ(lastAggregatedDay) + ", type="
                  + type.getRawTable());

            }



          } else if (period > 1) {

            // result += periodTermAggregationDao.aggregateTermsFromRawData(topicId,
            // new Date(date.getMillis()), period, type);

            result +=
                periodTermAggregationDao.aggregateTermsFromDaysPeriods(topicId, mnDate, period,
                    type);

            logger.info("Aggeragetd " + result + " rows from DAYS data for topicId=" + topicId
                + " , date=" + DateUtils.printDateTZ(mnDate) + ", period=" + period + ", type="
                + type.name());

            // clean old preaggregated data excepts period=1
            delResult +=
                periodTermAggregationDao.cleanupPeriodAgregationForTopic(topicId, mnDate, period,
                    type);

            logger.info("Cleaned " + delResult + " rows for topicId=" + topicId + " , date="
                + DateUtils.printDateTZ(mnDate) + ", period=" + period + ", type=" + type.name());

          }

          return result;

        } finally {
          perfPoint1.collect();
        }

      } else {
        return 0;
      }

    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public List<PeriodTermAggregate> getAggregationStatsPart(BigInteger topicId, DateTime date,
      int period, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type) {

    EtmPoint perfPoint = getPerformancePoint(".getAggregationStatsPart()");

    try {

      return periodTermAggregationDao.getAggregationStats(topicId, date, period, type, 0);
    } finally {
      perfPoint.collect();
    }
  }



  @Override
  public List<PeriodTermAggregate> getAggregationStatsForTopicDate(BigInteger topicId, DateTime date) {
    EtmPoint perfPoint = getPerformancePoint(".getAggregationStatsForTopicDate()");

    List<PeriodTermAggregate> statsList = new ArrayList<PeriodTermAggregate>();

    try {

      for (PeriodTermAggregate.AGGREGATE_TYPE_MAPPING type : PeriodTermAggregate.AGGREGATE_TYPE_MAPPING
          .values()) {
        int period = 1;
        statsList.addAll(getAggregationStatsPart(topicId, date, period, type));

        period = 7;
        statsList.addAll(getAggregationStatsPart(topicId, date, period, type));


        period = 30;
        statsList.addAll(getAggregationStatsPart(topicId, date, period, type));
      }


      return statsList;
    } finally {
      perfPoint.collect();
    }
  }

  private ObjectNode generateCLusterJSON(final List<Cluster> clusters,
      final Map<String, Long> wordCount, final List<CoOccurrenceInfo> wordsPairs,
      final Map<String, String> stemmingMap) throws JsonGenerationException, JsonMappingException,
      IOException {
    final ObjectMapper mapper = new ObjectMapper();
    final ObjectNode node = mapper.createObjectNode();

    if (clusters != null && clusters.size() > 0) {
      final ArrayNode arrayParent = mapper.createArrayNode();
      final ArrayNode arrayCluster = mapper.createArrayNode();
      node.put(CHILDREN, arrayParent);
      node.put(JSON_CLUSTERS, arrayCluster);
      for (final Cluster cluster : clusters) {
        final Set<String> nodes = cluster.getWords();
        final ArrayNode clArr = arrayCluster.addArray();
        for (CoOccurrenceInfo pair : wordsPairs) {
          final String secondWord = pair.getSecondWord();
          final String firstWord = pair.getFirstWord();
          if (nodes.contains(firstWord) && nodes.contains(secondWord)) {
            clArr.add(putRelation(mapper, firstWord, secondWord, pair.getCountLinks(), wordCount));
          } else if (nodes.contains(firstWord)) {
            clArr.add(putRelation(mapper, firstWord, firstWord, wordCount));
          } else if (nodes.contains(secondWord)) {
            clArr.add(putRelation(mapper, secondWord, secondWord, wordCount));
          }
        }
        final ObjectNode obj = mapper.createObjectNode();
        final ArrayNode array = mapper.createArrayNode();
        obj.put(CHILDREN, array);
        for (String str : cluster.getWords()) {
          final ObjectNode leaf = mapper.createObjectNode();
          leaf.put(JSON_NAME, str);
          leaf.put(SIZE, wordCount.containsKey(str) ? wordCount.get(str) : 0);
          array.add(leaf);
        }
        arrayParent.add(obj);
      }
    }
    if (stemmingMap != null) {
      node.put(STEMMING_MAP, mapper.writeValueAsString(stemmingMap));
    }
    return node;
  }

  @Deprecated
  @Override
  public List<TopicTweetAggregate> getLatestTweets(BigInteger topicId, String term,
      DateMidnight date, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING aggregateTypeMapping) {

    EtmPoint perfPoint = getPerformancePoint(".getLatestTweets()");

    try {
      if (date.isEqual(DateUtils.getCurrentMidnight().getMillis())) {
        return periodTermAggregationDao.selectLatestTweetsFromRawTable(topicId, term, date,
            aggregateTypeMapping);
      } else {
        date = date.plusDays(1);
        return periodTermAggregationDao.selectLatestTweetsFromPeriodTable(topicId, term, date,
            aggregateTypeMapping, 1);
      }
    } finally {
      perfPoint.collect();
    }
  }

  @Deprecated
  @Override
  public List<TopicTweetAggregate> getLatestTweetsByRange(BigInteger topicId, String term,
      int period, PeriodTermAggregate.AGGREGATE_TYPE_MAPPING aggregateTypeMapping) {
    EtmPoint perfPoint = getPerformancePoint(".getLatestTweetsByRange()");

    try {
      if (period == 1) {
        return periodTermAggregationDao.selectLatestTweetsFromRawTable(topicId, term,
            DateUtils.getCurrentMidnight(), aggregateTypeMapping);
      } else {
        return periodTermAggregationDao.selectLatestTweetsFromPeriodTable(topicId, term,
            DateUtils.getCurrentMidnight(), aggregateTypeMapping, period);
      }
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public TopicTermAggregate getLatestTweetIds(BigInteger topicId, String term, DateMidnight date,
                                              PeriodTermAggregate.AGGREGATE_TYPE_MAPPING aggregateTypeMapping) {

    EtmPoint perfPoint = getPerformancePoint(".getLatestTweets()");

    try {
      if (date.isEqual(DateUtils.getCurrentMidnight().getMillis())) {
        return periodTermAggregationDao.selectLatestTweetIdsFromRawTable(topicId, term, date,
            aggregateTypeMapping);
      } else {
        date = date.plusDays(1);
        return periodTermAggregationDao.selectLatestTweetIdsFromPeriodTable(topicId, term, date,
            aggregateTypeMapping, 1);
      }
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public TopicTermAggregate getLatestTweetIdsByRange(BigInteger topicId, String term, int period,
      PeriodTermAggregate.AGGREGATE_TYPE_MAPPING aggregateTypeMapping) {
    EtmPoint perfPoint = getPerformancePoint(".getLatestTweetsByRange()");

    try {
      if (period == 1) {
        return periodTermAggregationDao.selectLatestTweetIdsFromRawTable(topicId, term,
            DateUtils.getCurrentMidnight(), aggregateTypeMapping);
      } else {
        return periodTermAggregationDao.selectLatestTweetIdsFromPeriodTable(topicId, term,
            DateUtils.getCurrentMidnight(), aggregateTypeMapping, period);
      }
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void aggregateTerms() {
    EtmPoint perfPoint = getPerformancePoint(".aggregateTerms()");
    try {
      aggregateAllTopicsTermsForToday();
    } finally {
      perfPoint.collect();
    }
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(TermsAggregationServiceImpl.class
        .toString()).append(name).toString());
  }


  private static ObjectNode putRelation(ObjectMapper mapper, final String firstWord,
      final String secondWord, final Map<String, Long> wordCount) {
    return putRelation(mapper, firstWord, secondWord, Long.MIN_VALUE, wordCount);
  }

  private static ObjectNode putRelation(ObjectMapper mapper, final String firstWord,
      final String secondWord, long soOccurrency, final Map<String, Long> wordCount) {
    final ObjectNode obj = mapper.createObjectNode();

    ObjectNode source = obj.putObject("source");
    ObjectNode target = obj.putObject("target");

    source.put(JSON_NAME, firstWord);
    target.put(JSON_NAME, secondWord);

    source.put(SIZE, wordCount.containsKey(firstWord) ? wordCount.get(firstWord) : 0);
    target.put(SIZE, wordCount.containsKey(secondWord) ? wordCount.get(secondWord) : 0);

    if (soOccurrency > 0) {
      source.put(WEIGHT, soOccurrency);
    }

    return obj;
  }

  private static List<String> convertToList(final List<PeriodTermAggregate> aggregates) {
    final List<String> strings = new ArrayList<String>();
    if (aggregates != null && !aggregates.isEmpty()) {
      for (PeriodTermAggregate aggregate : aggregates) {
        final String term = aggregate.getTerm();
        if (term != null && term.length() > 0) {
          strings.addAll(Arrays.asList(term.split(SPACE)));
        }
      }
    }
    return Collections.unmodifiableList(strings);
  }
}
