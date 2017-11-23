package com.tfedorov.social.twitter.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.tfedorov.social.normalization.stemming.StemmingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.TopicType;
import com.tfedorov.social.topic.dao.TopicDao;
import com.tfedorov.social.topic.processing.TopicInfo;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.PeriodTermAggregationDao;
import com.tfedorov.social.twitter.aggregation.dao.TopicTermAggregate;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.tweet.Keyword;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service
public class TopicServiceImpl implements TopicService {

  private final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

  @Value(value = "${crawler.user:<crawler>}")
  private String industryUserName;

  // TODO: add limitation on UI for symbols can be entered for topics
  public static final String TOPIC_KEYWORDS_SEPARATOR = ",";

  public static final String TOPIC_PHRASE_UNION = " ";

  @Autowired
  private TopicDao topicDao;

  @Autowired
  private TweetsAggregationDao tweetsDao;
  @Autowired
  private PeriodTermAggregationDao periodTermsDao;
  @Autowired
  private StemmingService stemmingService;

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  @Override
  public void insertTopic(final Topic topic) {
    topicDao.insertTopic(topic);
  }

  @Override
  public Topic getTopicById(final BigInteger topicId) {
    return topicDao.getTopicById(topicId);
  }

  @Override
  public List<Topic> getByUserIdSorted(String userId, int offset, int limit, String orderBy,
      boolean isDesc) {

    return Collections.unmodifiableList(topicDao.getByUserIdSorted(userId, TopicType.CUSTOM,
        offset, limit, orderBy, isDesc));
  }

  @Override
  public List<Topic> getTypeTopics(TopicType type, int offset, int limit, String orderBy,
      boolean isDesc) {
    return Collections.unmodifiableList(topicDao.getTopicTypeSorted(type, offset, limit, orderBy,
        isDesc));
  }

  @Override
  public void markTopicAsDeleted(final Topic topicToDelete) {

    EtmPoint perfPoint = getPerformancePoint(".markTopicAsDeleted()");
    try {
      topicDao.markAsDelete(topicToDelete.getId());
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void asyncTopicAggregationClean(final Topic topicToDelete) {

    EtmPoint perfPoint = getPerformancePoint(".asyncTopicAggregationClean()");
    try {
      Runnable cleanTermsTask = new Runnable() {
        @Override
        public void run() {
          EtmPoint perfPoint1 = getPerformancePoint(".asyncTopicDelete():run()");
          try {
            // go sleep based on performance monitor (Wait while onStatus finish process last
            // status)
            Thread.sleep(360000);
            int rows = tweetsDao.cleanupAllAgregation(topicToDelete.getId());
            logger.info("For topicId=" + topicToDelete.getId() + " removed " + rows
                + " rows from aggregation tables and  preaggregation tables");

          } catch (Exception e) {
            logger.error("Error on async topic aggregation cleaner thread! ", e);
          } finally {
            perfPoint1.collect();
            logger.info("Deleting for topicId=" + topicToDelete.getId() + " took "
                + perfPoint1.getTransactionTime() + "ms");
          }
        }
      };

      Thread thread = new Thread(cleanTermsTask, "Async Delete Topic " + topicToDelete.getId());

      thread.start();

    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public TopicInfo createTopicInfo(Topic topic) {
    // generate kayword lists
    List<Keyword> parsedKeywordsLCWSList = new ArrayList<Keyword>();
    Set<String> wordsSetLCSet = new HashSet<String>();
    Set<String> stemmedWordsSetLCSet = new HashSet<String>();

    StringTokenizer tokenizer = new StringTokenizer(topic.getKeywords(), TOPIC_KEYWORDS_SEPARATOR);
    while (tokenizer.hasMoreTokens()) {
      Keyword kw = new Keyword();
      String keyWord = tokenizer.nextToken().trim().toLowerCase();
      kw.setNormalKeyword(" " + keyWord + " ");
      String stemmedKw =
          stemmingService.stemWithoutHistory(keyWord, StemmingService.DEFAULT_LANGUAGE);
      kw.setStemmedKeyword(" " + stemmedKw + " ");

      parsedKeywordsLCWSList.add(kw);
      //
      StringTokenizer wordTokenizer = new StringTokenizer(keyWord, TOPIC_PHRASE_UNION);
      while (wordTokenizer.hasMoreTokens()) {
        String word = wordTokenizer.nextToken();
        wordsSetLCSet.add(word);
        if (wordTokenizer.countTokens() > 1) {
          stemmedWordsSetLCSet.add(stemmingService.stemWithoutHistory(keyWord,
              StemmingService.DEFAULT_LANGUAGE).trim());
        } else {
          stemmedWordsSetLCSet.add(stemmedKw);
        }
      }
    }
    parsedKeywordsLCWSList = Collections.unmodifiableList(parsedKeywordsLCWSList);
    wordsSetLCSet = Collections.unmodifiableSet(wordsSetLCSet);
    stemmedWordsSetLCSet = Collections.unmodifiableSet(stemmedWordsSetLCSet);
    //
    return new TopicInfo(topic, parsedKeywordsLCWSList, wordsSetLCSet, stemmedWordsSetLCSet);
  }

  @Override
  public void asyncTopicUpdate(Topic updatedTopic) {

    EtmPoint perfPoint = getPerformancePoint(".asyncTopicUpdate()");
    try {
      final Topic oldTopic = topicDao.getTopicById(updatedTopic.getId());

      topicDao.updateTopic(updatedTopic);

      final Set<String> newKeywordsToClean = checkTermsShouldClean(oldTopic, updatedTopic);

      if (!newKeywordsToClean.isEmpty()) {
        logger.info("Will update terms for topicId=" + oldTopic.getId() + " to remove keywords:"
            + newKeywordsToClean);

        // TODO: rewrite with using threadpool executor or quartz job to
        // fire

        Runnable cleanTermsTask = new Runnable() {
          @Override
          public void run() {
            EtmPoint perfPoint1 = getPerformancePoint(".asyncTopicUpdate():run()");
            try {
              for (String keyWord : newKeywordsToClean) {
                int rows =
                    tweetsDao.cleanupTerms(oldTopic.getId(),
                        TopicTermAggregate.AGGREGATE_TYPE.topic_terms, keyWord);
                int rowsP =
                    periodTermsDao.cleanupPeriodTerms(oldTopic.getId(),
                        PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p, keyWord);
                logger.info("For topicId=" + oldTopic.getId() + " removed " + rows
                    + " rows by keyword [" + keyWord + "] from "
                    + TopicTermAggregate.AGGREGATE_TYPE.topic_terms.name() + " and " + rowsP
                    + " form " + PeriodTermAggregate.AGGREGATE_TYPE_MAPPING.topic_terms_p
                    + " table");
              }
            } finally {
              perfPoint1.collect();
              logger.info("Cleanup for topicId=" + oldTopic.getId() + " took "
                  + perfPoint1.getTransactionTime() + "ms");
            }

          }
        };

        Thread thread = new Thread(cleanTermsTask, "Async Update Topic " + updatedTopic.getId());

        thread.start();
      }
    } finally {
      perfPoint.collect();
    }

  }

  private Set<String> checkTermsShouldClean(Topic oldTopic, Topic newTopic) {
    // TODO: revisit for better performance

    Set<String> result = new HashSet<String>();

    TopicInfo oldTopicInfo = createTopicInfo(oldTopic);
    TopicInfo newTopicInfo = createTopicInfo(newTopic);

    List<Keyword> oldKeyWordsList = oldTopicInfo.getParsedKeywordsLCWSList();
    List<Keyword> newKeyWordsList = newTopicInfo.getParsedKeywordsLCWSList();

    for (Keyword newKeyWord : newKeyWordsList) {
      for (Keyword oldKeyWord : oldKeyWordsList) {
        if (!oldKeyWord.getNormalKeyword().equals(newKeyWord.getNormalKeyword())) {
          result.add(newKeyWord.getNormalKeyword().trim());
          break;
        }
      }
    }

    return result;
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(TopicServiceImpl.class.toString())
        .append(name).toString());
  }

}
