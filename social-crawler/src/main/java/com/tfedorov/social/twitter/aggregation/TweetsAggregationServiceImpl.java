package com.tfedorov.social.twitter.aggregation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;

import com.tfedorov.social.intention.IntentionService;
import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.processing.UsefulTweetsCounterHandler;
import com.tfedorov.social.topic.dao.TopicDao;
import com.tfedorov.social.topic.processing.TopicTaskExecutionHandler;
import com.tfedorov.social.twitter.aggregation.dao.TweetsAggregationDao;
import com.tfedorov.social.twitter.processing.ServicesContext;
import com.tfedorov.social.twitter.processing.ServicesContextImpl;
import com.tfedorov.social.twitter.processing.concarrency.ConcurrencyProtectionHandler;
import com.tfedorov.social.twitter.processing.filtering.BlackListFilterHandler;
import com.tfedorov.social.twitter.processing.filtering.LanguageFilterHandler;
import com.tfedorov.social.twitter.processing.filtering.TrendFrequentFilterHandler;
import com.tfedorov.social.twitter.processing.intention.IntentDetectionHandler;
import com.tfedorov.social.twitter.processing.intention.IntentionProcessingHandler;
import com.tfedorov.social.twitter.processing.retweet.TopRetweetsHandler;
import com.tfedorov.social.twitter.processing.terms.TweetTrendTermsHandler;
import com.tfedorov.social.twitter.processing.trace.DumpTweetHandler;
import com.tfedorov.social.twitter.processing.tweet.FindKeywordsHandler;
import com.tfedorov.social.twitter.processing.tweet.TrendsKeywordsHandler;
import com.tfedorov.social.twitter.processing.tweet.TweetInfoBuilder;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import com.tfedorov.social.word.dao.WordsDao;
import com.tfedorov.social.word.processing.WordProcessingContext;
import com.tfedorov.social.word.processing.WordsInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import twitter4j.Status;

import com.tfedorov.social.concurrency.TaskExecutionService;
import com.tfedorov.social.intention.processing.IntentionProcessingContext;
import com.tfedorov.social.processing.TerminalHandler;
import com.tfedorov.social.topic.Topic;
import com.tfedorov.social.topic.TopicType;
import com.tfedorov.social.topic.processing.TopicInfo;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.GeneralProcessingContextImpl;
import com.tfedorov.social.twitter.processing.filtering.IndustryFilterHandler;
import com.tfedorov.social.twitter.processing.filtering.NormalizationFilterHandler;
import com.tfedorov.social.twitter.processing.filtering.TrendFilterHandler;
import com.tfedorov.social.twitter.processing.mention.MentionsHandler;
import com.tfedorov.social.twitter.processing.sentiments.SentimentHandler;
import com.tfedorov.social.twitter.processing.terms.TweetTermsHandler;
import com.tfedorov.social.twitter.sentiments.strategy.SentimentStrategy;
import com.tfedorov.social.twitter.service.TopicService;
import com.tfedorov.social.twitter.tracing.TweetTracingService;
import com.tfedorov.social.word.Word;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service
public class TweetsAggregationServiceImpl implements TweetsAggregationService {

  private static Logger logger = LoggerFactory.getLogger(TweetsAggregationServiceImpl.class);

  @Autowired
  private TopicDao topicDao;

  @Autowired
  private WordsDao wordsDao;

  @Autowired
  private TweetsAggregationDao tweetsAggregationDao;

  @Autowired
  private IntentionService intentionServiceImpl;

  @Autowired
  private TweetTracingService tweetTracingService;

  @Autowired
  private TaskExecutionService taskExecutionService;

  @Autowired
  private StemmingService stemmingService;

  @Autowired
  private TopicService topicService;

  @Autowired
  @Qualifier(value = "qualifSentimentStrategy")
  private SentimentStrategy sentimentStrategy;

  @Autowired
  @Value("${crawler.language.support.list:en}")
  private String supportLanguagesLine;

  private volatile List<TopicInfo> topicsInfoList = new ArrayList<TopicInfo>();

  private volatile WordsInfo wordsInfo = new WordsInfo();

  private volatile Set<String> languagesSupported = new HashSet<String>();

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private ProcessingHandler<GeneralProcessingContext> startHandler;


  /**
   * test purposes
   * 
   * @return the startHandler
   */
  public ProcessingHandler<GeneralProcessingContext> getStartHandler() {
    return startHandler;
  }

  @Override
  @PostConstruct
  public void init() {
    EtmPoint perfPoint = getPerformancePoint(".init()");
    try {
      initWords();

      initTopics();

      initLanguages();

      buildProcessingChain();

      // init normalization analyzers
      stemmingService.initializeAnalyzers(wordsInfo.getStopWords());
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public void processStatus(Status status) {
    EtmPoint perfPoint = getPerformancePoint(".processStatus()");
    try {

      // prepare contexts
      WordProcessingContext wordsContext = new WordProcessingContext(wordsInfo);

      TweetProcessingContext tweetContext =
          new TweetProcessingContext(TweetInfoBuilder.buildFromTwit4j(status));

      // TODO: add intention info to constructor
      // not used yet
      IntentionProcessingContext intentionContext = new IntentionProcessingContext();

      ServicesContext servicesContext =
          new ServicesContextImpl(tweetsAggregationDao, intentionServiceImpl, tweetTracingService,
              taskExecutionService, sentimentStrategy, stemmingService);

      GeneralProcessingContextImpl processingContext =
          new GeneralProcessingContextImpl(tweetContext, wordsContext, intentionContext,
              servicesContext);

      processingContext.add(GeneralProcessingContextImpl.TOPICS_INFO_LIST, topicsInfoList);

      processingContext.add(GeneralProcessingContextImpl.LANGUAGES_SUPPORTED, languagesSupported);

      startHandler.process(processingContext);

    } finally {
      perfPoint.collect();
    }

  }

  protected void buildProcessingChain() {

    EtmPoint perfPoint = getPerformancePoint(".buildProcessingChain()");

    try {

      ProcessingHandler<GeneralProcessingContext> terminalHandler =
          new TerminalHandler<GeneralProcessingContext>();

      ProcessingHandler<GeneralProcessingContext> usefulTweetsCounterHandler =
          new UsefulTweetsCounterHandler(terminalHandler);

      ProcessingHandler<GeneralProcessingContext> industryFilterHandler =
          new IndustryFilterHandler(buildProcessingChainIndustry(terminalHandler),
              buildProcessingChainCustom(terminalHandler));

      ProcessingHandler<GeneralProcessingContext> trendFilterHandler =
          new ConcurrencyProtectionHandler(new TrendFilterHandler(
              buildProcessingChainTrend(terminalHandler), industryFilterHandler));

      ProcessingHandler<GeneralProcessingContext> topicsConcurrentHandler =
          new TopicTaskExecutionHandler(trendFilterHandler, usefulTweetsCounterHandler);

      ProcessingHandler<GeneralProcessingContext> intentDetectionHandler =
          new IntentDetectionHandler(topicsConcurrentHandler);

      ProcessingHandler<GeneralProcessingContext> sentimentHandler =
          new SentimentHandler(intentDetectionHandler);

      ProcessingHandler<GeneralProcessingContext> normalizaTionHandler =
          new NormalizationFilterHandler(sentimentHandler);

      ProcessingHandler<GeneralProcessingContext> languageFiterHandler =
          new LanguageFilterHandler(normalizaTionHandler, terminalHandler);

      ProcessingHandler<GeneralProcessingContext> blackListHandler =
          new BlackListFilterHandler(terminalHandler, languageFiterHandler);

      ProcessingHandler<GeneralProcessingContext> dumpTweetHendler =
          new DumpTweetHandler(blackListHandler);

      // First handler for tweet processing
      this.startHandler = dumpTweetHendler;

    } finally {
      perfPoint.collect();
    }
  }

  protected ProcessingHandler<GeneralProcessingContext> buildProcessingChainCustom(
      ProcessingHandler<GeneralProcessingContext> terminalHandler) {

    ProcessingHandler<GeneralProcessingContext> intentionProcessingHandler =
        new IntentionProcessingHandler(terminalHandler);

    ProcessingHandler<GeneralProcessingContext> tweetTermsHandler =
        new TweetTermsHandler(intentionProcessingHandler);

//    ProcessingHandler<GeneralProcessingContext> latesTweetsHandler =
//        new LatesTweetsHandler(tweetTermsHandler);

    ProcessingHandler<GeneralProcessingContext> topRetweetsHandler =
        new TopRetweetsHandler(tweetTermsHandler/*tweetTermsHandler*/);

    ProcessingHandler<GeneralProcessingContext> mentionsHandler =
        new MentionsHandler(topRetweetsHandler);

    ProcessingHandler<GeneralProcessingContext> findKeyWordHandler =
        new FindKeywordsHandler(mentionsHandler);

    return findKeyWordHandler;

  }

  protected ProcessingHandler<GeneralProcessingContext> buildProcessingChainTrend(
      ProcessingHandler<GeneralProcessingContext> terminalHandler) {
	  //Commented by performance purposes
/*
    ProcessingHandler<GeneralProcessingContext> latesTweetsHandler =
        new LatesTweetsHandler(terminalHandler);

    ProcessingHandler<GeneralProcessingContext> tweetTrendTermsHandler =
        new TweetTrendTermsHandler(latesTweetsHandler);
*/

	    ProcessingHandler<GeneralProcessingContext> tweetTrendTermsHandler =
	            new TweetTrendTermsHandler(terminalHandler);
	    
    ProcessingHandler<GeneralProcessingContext> trendProcessingHandler =
        new TrendsKeywordsHandler(tweetTrendTermsHandler);

    ProcessingHandler<GeneralProcessingContext> trendFrequentHandler =
        new TrendFrequentFilterHandler(trendProcessingHandler, terminalHandler);

    return trendFrequentHandler;
  }

  protected ProcessingHandler<GeneralProcessingContext> buildProcessingChainIndustry(
      ProcessingHandler<GeneralProcessingContext> terminalHandler) {

//    ProcessingHandler<GeneralProcessingContext> latesTweetsHandler =
//        new LatesTweetsHandler(terminalHandler);

    ProcessingHandler<GeneralProcessingContext> mentionsIndustryHandler =
        new MentionsHandler(terminalHandler);

    ProcessingHandler<GeneralProcessingContext> tweetTrendTermsHandler =
        new TweetTermsHandler(mentionsIndustryHandler);

    ProcessingHandler<GeneralProcessingContext> keyWordHandler =
        new FindKeywordsHandler(tweetTrendTermsHandler);

    return keyWordHandler;
  }

  @Override
  public JSONObject getChainStructure() {
    try {
      return startHandler.returnJson();
    } catch (JSONException e) {
      logger.error("Exception when try create chain structure." + e);
      return new JSONObject();
    }
  }

  protected void initTopics() {

    EtmPoint perfPoint = getPerformancePoint(".initTopicList()");

    try {
      List<TopicInfo> tmpTopicsInfoList = new ArrayList<TopicInfo>();
      List<Topic> topics = topicDao.getTrackedTopics();

      for (Topic topic : topics) {
        tmpTopicsInfoList.add(topicService.createTopicInfo(topic));
      }

      // prevent modification within processing
      tmpTopicsInfoList = Collections.unmodifiableList(tmpTopicsInfoList);

      // reference assignment is atomic, volatile used to prevent from copy
      this.topicsInfoList = tmpTopicsInfoList;

      logger.info("Topic count=" + topicsInfoList.size());
      logger.debug("Topics trecked:" + topicsInfoList);

    } finally {
      perfPoint.collect();
    }
  }

  protected void initWords() {

    EtmPoint perfPoint = getPerformancePoint(".initWords()");

    try {
      List<String> tmpStopWordsList = wordsDao.selectWordStrings(Word.WORD_TYPE.stop_words);
      List<String> tmpBlackWordsList = wordsDao.selectWordStrings(Word.WORD_TYPE.black_words);

      // TODO: move logic bellow to separate class/method for better testability

      // compose set of stopWords
      Set<String> tmpStopWordsSet = new HashSet<String>(tmpStopWordsList.size());

      for (String word : tmpStopWordsList) {
        tmpStopWordsSet.add(word.trim().toLowerCase()); // if value in DB isn't in lower case
      }

      // compose set of blackWords -
      // Set<String> tmpBlackWordsSet = new HashSet<String>(tmpBlackWordsList);


      // if blackword is phrase (contains few words) we adding original and concatenated variant
      Set<String> tmpBlackWordsSet = new HashSet<String>();
      for (String word : tmpBlackWordsList) {
        tmpBlackWordsSet.add(word.trim().toLowerCase()); // if value in DB
        // isn't in
        // lower case
        if (word.indexOf(" ") >= 0) {
          // TODO: may rewrite with regexp
          StringTokenizer tokenizer = new StringTokenizer(word, " ");
          StringBuilder builder = new StringBuilder(tokenizer.nextToken());
          while (tokenizer.hasMoreTokens()) {
            builder.append(tokenizer.nextToken());
          }

          tmpBlackWordsSet.add(builder.toString().toLowerCase());
        }
      }

      // prevent modification within processing
      tmpStopWordsSet = Collections.unmodifiableSet(tmpStopWordsSet);
      tmpBlackWordsSet = Collections.unmodifiableSet(tmpBlackWordsSet);

      WordsInfo tmpWordsInfo = new WordsInfo(tmpStopWordsSet, tmpBlackWordsSet);

      // reference assignment is atomic, volatile used to prevent from copy
      this.wordsInfo = tmpWordsInfo;

      logger.info("Stop Words count=" + wordsInfo.getStopWords().size() + ", Black Words count="
          + wordsInfo.getBlackWords().size());
      logger.debug("Words Checked:" + wordsInfo);
    } finally {
      perfPoint.collect();
    }
  }

  protected void initLanguages() {
    Set<String> tmpLanguagesSupported = new HashSet<String>();
    StringTokenizer str = new StringTokenizer(supportLanguagesLine, ",");
    while (str.hasMoreTokens()) {
      tmpLanguagesSupported.add(str.nextToken().trim());
    }
    tmpLanguagesSupported = Collections.unmodifiableSet(tmpLanguagesSupported);

    languagesSupported = tmpLanguagesSupported;

  }

  @Override
  public void reloadTopics() {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(TweetsAggregationServiceImpl.class + ".reloadTopics()");
    try {
      initTopics();
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void reloadWords() {
    EtmPoint perfPoint = getPerformancePoint(".reloadWords()");
    try {
      initWords();
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void updateTopics() {
    EtmPoint perfPoint = getPerformancePoint(".updateTopics()");

    try {
      List<Topic> topicsToCheck = topicDao.getTrackedTopics();
      for (Topic topic : topicsToCheck) {
        refreshTopicStatus(topic);
      }

      // TODO: may not update on every status update
      reloadTopics();
    } finally {
      perfPoint.collect();
    }
  }

  public int getTopicsNumber() {
    return topicsInfoList.size();
  }

  private void refreshTopicStatus(Topic topic) {

    EtmPoint perfPoint = getPerformancePoint(".refreshTopicStatus()");

    try {
      BigInteger topicId = topic.getId();

      if (topic.getType() == TopicType.CUSTOM) {
        int status = 0;
        boolean hasPopular = checkTable(topicId, TweetsAggregationDao.TABLE_NAME.popular_tweets);
        boolean hasMentions = checkTable(topicId, TweetsAggregationDao.TABLE_NAME.topic_mentions);
        boolean hasTerms = checkTable(topicId, TweetsAggregationDao.TABLE_NAME.topic_terms);
        boolean hasTermsP = checkTable(topicId, TweetsAggregationDao.TABLE_NAME.topic_terms_p);

        if (hasMentions && hasPopular && (hasTerms || hasTermsP)) {
          status = 3;
        } else if (!hasMentions && !hasPopular && !hasTerms) {
          status = 0;
        } else {
          status = 1;
        }

        topicDao.updateStatus(topicId, status);
      }

    } finally {
      perfPoint.collect();
    }

  }

  private boolean checkTable(BigInteger topicId, TweetsAggregationDao.TABLE_NAME tableName) {
    return tweetsAggregationDao.checkTableForStatus(topicId, tableName).size() > 0;
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(TweetsAggregationServiceImpl.class
        .toString()).append(name).toString());
  }

  @Override
  public void deleteTopic(Topic topic) {
    EtmPoint perfPoint = getPerformancePoint(".deleteTopic()");

    try {
      topicService.markTopicAsDeleted(topic);
      // reload topics
      reloadTopics();
      // async clear all topic aggregated data
      topicService.asyncTopicAggregationClean(topic);
    } finally {
      perfPoint.collect();
    }
  }
}
