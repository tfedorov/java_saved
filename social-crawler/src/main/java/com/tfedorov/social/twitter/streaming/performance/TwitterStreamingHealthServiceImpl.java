package com.tfedorov.social.twitter.streaming.performance;

import com.tfedorov.social.twitter.streaming.TwitterStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("twitterStreamingHealthService")
class TwitterStreamingHealthServiceImpl implements TwitterStreamingHealthService {

  private Logger logger = LoggerFactory.getLogger(TwitterStreamingHealthServiceImpl.class);

  public static final long KB = 1024L;

  public static final long MB = 1024L * KB;

  public static final long HIGH_TWEET_SIZE = 5 * KB; // by our observations tweets avg size 2-4Kb,
                                                     // will use 5Kb as high value

  @Autowired
  private TwitterStreamingService twitterStreamingService;

  private int freeMemoryThresholdLow;

  private int freeMemoryThresholdHigh;

  private int maxTweetQueueSize;

  private int minTweetQueueSize;

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private boolean active;

  @Autowired
  public TwitterStreamingHealthServiceImpl(
      @Value("${crawler.health.fmemory.threshold.low:10}") int freeMemoryThresholdLow,
      @Value("${crawler.health.fmemory.threshold.high:40}") int freeMemoryThresholdHigh,
      @Value("${crawler.health.autostart:true}") boolean autostart,
      @Value("${crawler.health.max.tweet.queue:150000}") int maxTweetQueueSize,
      @Value("${crawler.health.min.tweet.queue:50000}") int minTweetQueueSize) {
    //
    this.freeMemoryThresholdLow = freeMemoryThresholdLow;
    this.freeMemoryThresholdHigh = freeMemoryThresholdHigh;
    this.maxTweetQueueSize = maxTweetQueueSize;
    this.minTweetQueueSize = minTweetQueueSize;
    this.active = autostart;
    //
    logger.info("[Low Free Memory Threshold:" + freeMemoryThresholdLow
        + "%, High Free Memory Threshold:" + freeMemoryThresholdHigh + "% ]");
  }

  @Override
  public void check() {
    EtmPoint perfPoint = getPerformancePoint(".check()");
    try {
      // check memory - try to free if GC strategy isn't enough
      if (isVolumeToFree()) {
        tryToForceGC();
      }
      // check for active
      if (active) {
        int freeMemory = getFreeMemoryInPercentage();
        int tweetQueue = getTweetQueueSize();
        // check system memory
        if ((freeMemory < freeMemoryThresholdLow || tweetQueue > maxTweetQueueSize)
            && twitterStreamingService.isActive()) {
          logger.warn("Low free memory: " + freeMemory + "% or tweet queue too big: " + tweetQueue
              + " tweets. Healther will suspend Twitter Consumer stream");

          suspendStreaming();

        } else if (freeMemory > freeMemoryThresholdHigh && tweetQueue < minTweetQueueSize
            && !twitterStreamingService.isActive()) {
          logger.warn("Free memory renewed to: " + freeMemory + "% or queue decrease: "
              + tweetQueue + ". Healther will resume Twitter Consumer stream");

          resumeStreaming();

        }
        // check
      }
    } finally {
      perfPoint.collect();
    }
  }

  private int getTweetQueueSize() {
    return twitterStreamingService.getTweetsQueueSize();
  }

  @Override
  public int getFreeMemoryInPercentage() {

    long mbFree = Runtime.getRuntime().freeMemory() / MB;
    long mbTotal = Runtime.getRuntime().totalMemory() / MB;

    double pFree = (double) mbFree / (double) mbTotal;

    return (int) (pFree * 100);
  }

  @Override
  public long getUsedMemoryInKB() {

    long kbUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / KB;

    return kbUsed;
  }

  protected boolean isVolumeToFree() {

    // estimate memory usage by tweets in memory queue (inside twitter4j library)
    long estimatedUsedMemoryKB = twitterStreamingService.getTweetsQueueSize() * HIGH_TWEET_SIZE;
    long usedMemoryKb = getUsedMemoryInKB();

    return (estimatedUsedMemoryKB < usedMemoryKb) && (usedMemoryKb / KB > 200);
  }

  protected void tryToForceGC() {
    EtmPoint perfPoint = getPerformancePoint(".tryToForceGC()");
    try {
      System.gc();
    } finally {
      perfPoint.collect();
    }
  }

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(TwitterStreamingHealthServiceImpl.class
        .toString()).append(name).toString());
  }

  protected void suspendStreaming() {
    EtmPoint perfPoint = getPerformancePoint(".suspendStreaming()");

    try {
      twitterStreamingService.stopCrawler();
    } finally {
      perfPoint.collect();
    }
  }

  protected void resumeStreaming() {
    EtmPoint perfPoint = getPerformancePoint(".resumeStreaming()");

    try {
      twitterStreamingService.startCrawler();
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void stop() {
    EtmPoint perfPoint = getPerformancePoint(".stop()");
    try {
      active = false;
      logger.info("Stop");
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public void start() {
    EtmPoint perfPoint = getPerformancePoint(".start()");
    try {
      active = true;
      logger.info("Start");
    } finally {
      perfPoint.collect();
    }
  }

}
