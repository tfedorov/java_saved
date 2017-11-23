package com.tfedorov.social.twitter.streaming;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.tfedorov.social.utils.date.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import com.tfedorov.social.twitter.aggregation.TweetsAggregationService;
import com.tfedorov.social.twitter.tracing.TweetTracingService;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("twitterStreamingService")
public class TwitterStreamingServiceImpl implements TwitterStreamingService {

  private static Logger logger = LoggerFactory.getLogger(TwitterStreamingServiceImpl.class);

  private StatusListener statusListener;

  private String sOAuthConsumerKey;

  private String sOAuthConsumerSecret;

  private AccessToken accessToken;

  private TwitterStream twitterStream;

  private TweetsAggregationService aggregationService;

  private TweetTracingService tracingService;

  private boolean autoStart = true;

  private volatile boolean active = false;

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private Deque<RunPeriod> runInfo = new LinkedBlockingDeque<RunPeriod>();

  @Autowired
  public TwitterStreamingServiceImpl(
      @Value("${agg.twitter.consumer.key}") String sOAuthConsumerKey,
      @Value("${agg.twitter.consumer.secret}") String sOAuthConsumerSecret,
      @Value("${agg.twitter.access.token}") String sOAuthAccessToken,
      @Value("${agg.twitter.access.secret}") String sOAuthAccessTokenSecret,
      @Value("${twitter.crawler.autostart:true}") boolean autoStart,
      TweetsAggregationService aggregationService, TweetTracingService tracingService) {

    this.sOAuthConsumerKey = sOAuthConsumerKey;
    this.sOAuthConsumerSecret = sOAuthConsumerSecret;
    this.accessToken = new AccessToken(sOAuthAccessToken, sOAuthAccessTokenSecret);
    this.aggregationService = aggregationService;
    this.tracingService = tracingService;
    this.statusListener = new TwitterStatusListener(aggregationService, tracingService);
    this.autoStart = autoStart;

  }

  // @Override
  // @PostConstruct
  // public void init() {
  // logger.info("[INIT CRAWLER!]");
  // twitterStream = new TwitterStreamFactory().getInstance();
  // twitterStream.setOAuthConsumer(sOAuthConsumerKey, sOAuthConsumerSecret);
  // twitterStream.setOAuthAccessToken(accessToken);
  // twitterStream.addListener(statusListener);
  // logger.info("CRAWLER CONFIGURATION:" + twitterStream.getConfiguration());
  // if (autoStart) {
  // startCrawler();
  // }
  // }

  @Override
  @PostConstruct
  public void init() {
    EtmPoint perfPoint = getPerformancePoint(".init()");

    logger.info("[INIT CRAWLER!]");
    try {
      ConfigurationBuilder confBuilder = new ConfigurationBuilder();
      confBuilder.setOAuthConsumerKey(sOAuthConsumerKey)
          .setOAuthConsumerSecret(sOAuthConsumerSecret).setOAuthAccessToken(accessToken.getToken())
          .setOAuthAccessTokenSecret(accessToken.getTokenSecret())
          // enable json store to have possibility for raw json reading
          // TODO: may be enable only for not production
          .setJSONStoreEnabled(true);

      twitterStream = new TwitterStreamFactory(confBuilder.build()).getInstance();
      twitterStream.addListener(statusListener);

      logger.info("CRAWLER CONFIGURATION:" + twitterStream.getConfiguration());

      if (autoStart) {
        startCrawler();
      }

    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public void startCrawler() {
    EtmPoint perfPoint = getPerformancePoint(".startCrawler()");

    logger.info("[STARTING CRAWLER!]");
    try {
      twitterStream.sample();
      active = true;

      RunPeriod last = runInfo.peekLast();
      if (last == null || !last.getEndTime().equals(RunPeriod.NOW)) {
        runInfo.add(new RunPeriod());
      }

    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void stopCrawler() {
    EtmPoint perfPoint = getPerformancePoint(".stopCrawler()");

    logger.info("[STOPPING CRAWLER!]");
    try {
      twitterStream.cleanUp();
      RunPeriod last = runInfo.peekLast();
      if (last != null && last.getEndTime().equals(RunPeriod.NOW)) {
        last.fixEndTime();
      }

    } finally {
      active = false;
      perfPoint.collect();
    }

  }

  @Override
  @PreDestroy
  public void shutDownCrawler() {
    EtmPoint perfPoint = getPerformancePoint(".shutDownCrawler()");

    logger.info("[SHUTING DOWN CRAWLER!]");
    try {
      twitterStream.shutdown();

      RunPeriod last = runInfo.peekLast();
      if (last != null && last.getEndTime().equals(RunPeriod.NOW)) {
        last.fixEndTime();
      }

    } finally {
      active = false;
      perfPoint.collect();
    }

  }

  @Override
  public void reloadWords() {
    EtmPoint perfPoint = getPerformancePoint(".reloadWords()");
    logger.info("[RELOAD WORDS!]");
    try {
      aggregationService.reloadWords();
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void reloadTopics() {
    EtmPoint perfPoint = getPerformancePoint(".reloadTopics()");
    logger.info("[RELOAD TOPICS!]");
    try {
      aggregationService.reloadTopics();
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void updateTopics() {
    EtmPoint perfPoint = getPerformancePoint(".updateTopics()");

    logger.info("[UPDATE TOPICS!]");
    try {
      aggregationService.updateTopics();
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public int getTweetsQueueSize() {

    try {
      Object dispatcherValue = getFieldValFromClass(twitterStream, "dispatcher");
      if (dispatcherValue == null) {
        return -1;
      }
      List<Runnable> q = (List<Runnable>) getFieldValFromClass(dispatcherValue, "q");

      return q.size();
    } catch (NoSuchFieldException ex) {
      return Integer.MAX_VALUE;
    }
  }

  private Object getFieldValFromClass(Object target, String fieldName) throws NoSuchFieldException {
    final Field field = ReflectionUtils.findField(target.getClass(), fieldName);
    if (field == null) {
      logger.error("Could not find List<Runnable> 'q' in DispatcherImpl");
      throw new NoSuchFieldException(fieldName);
    }
    field.setAccessible(true);
    Object fieldValue = ReflectionUtils.getField(field, target);
    field.setAccessible(false);
    return fieldValue;
  }

  @Override
  public String getRunInfo() {
    return runInfo.toString();
  }

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(TwitterStreamingServiceImpl.class
        .toString()).append(name).toString());
  }

  public int getTopicsNumber() {
    return aggregationService.getTopicsNumber();
  }

  public static class RunPeriod {

    public static final String NOW = "now";

    private long startMs;
    private long endMs;
    private String startTime;
    private String endTime = NOW;

    public RunPeriod() {
      DateTime std = DateUtils.getCurrentDateTime();
      startMs = std.getMillis();
      startTime = DateUtils.printDateTZ(std);
    }

    public void fixEndTime() {
      DateTime std = DateUtils.getCurrentDateTime();
      endMs = std.getMillis();
      endTime = DateUtils.printDateTZ(std);
    }

    public String getStartTime() {
      return startTime;
    }

    public String getEndTime() {
      return endTime;
    }

    @Override
    public String toString() {
      if (endTime.equals(NOW)) {
        endMs = DateUtils.getCurrentDateTime().getMillis();
      }
      return "[" + startTime + "-" + endTime + "] - "
          + new DecimalFormat("#.##").format((double) (endMs - startMs) / (double) (3600 * 1000))
          + " hours\n<br>";
    }

  }
}
