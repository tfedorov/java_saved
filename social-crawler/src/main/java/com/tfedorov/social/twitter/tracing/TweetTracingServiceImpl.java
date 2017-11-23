package com.tfedorov.social.twitter.tracing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import com.tfedorov.social.utils.date.DateUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import twitter4j.Status;
import twitter4j.json.DataObjectFactory;

@Service
public class TweetTracingServiceImpl implements TweetTracingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetTracingServiceImpl.class);
  
  private int trendFrequency = 1;

  private String dumpDirectoryPath = "/opt/dmp-tweets/dump";
  
  private int dumpFrequency = -1;

  private int traceFrequency = 10000;

  private int speedCheckFrequency = traceFrequency / 10;

  private long stratMonitoringTime = 0;

  private long stratMonitoringTimePrev = 0;

  private long endMonitoringTime = 0;

  private double currentSpeed = 0;

  // Counter of procceded tweets
  private volatile int counter = 1;
  private volatile int usefullCounter = 0;


  private String lastRunDT = DateUtils.printDateTZ(DateUtils.getCurrentDateTime());
  private boolean dumpErrorTweets = false;

  @Autowired
  public TweetTracingServiceImpl(
      @Value("${twitter.trace.dump.dir:/opt/dmp-tweets/dump}") String dumpDirectoryPath,
      @Value("${twitter.trace.dump.frequency:0}") int dumpFrequency,
      @Value("${twitter.trace.tracing.frequency:10000}") int traceFrequency,
      @Value("${twitter.trace.error.tweets:false}") boolean dumpErrorTweets,
      @Value("${topic.trend.frequency:1}") int trendFrequency) {

    this.dumpDirectoryPath = dumpDirectoryPath;
    this.dumpFrequency = dumpFrequency;
    this.traceFrequency = traceFrequency;
    this.dumpErrorTweets = dumpErrorTweets;
    this.speedCheckFrequency = traceFrequency / 10;
    this.trendFrequency = trendFrequency;

  }

  @Override
  public void dumpTweetToFile(Status tweet) {
    dumpTweetToFile(tweet, false);
  }

  // Every tweet in process chain call this method
  @Override
  public void trace() {
    if (stratMonitoringTime == 0) {
      stratMonitoringTime = new Date().getTime();
      stratMonitoringTimePrev = stratMonitoringTime;
    }

    counter++;
    if ((counter % traceFrequency) == 0) {
      LOGGER.info(counter + " tweets processed from last start [" + lastRunDT + "]");
    }

    if ((counter % speedCheckFrequency) == 0) {
      endMonitoringTime = new Date().getTime();
      long measuredPeriod = endMonitoringTime - stratMonitoringTime;
      currentSpeed = (double) measuredPeriod / (double) speedCheckFrequency;
      stratMonitoringTimePrev = stratMonitoringTime;
      stratMonitoringTime = endMonitoringTime;

    }

  }

  public CurrentPerformanceBean getCurPerformance() {
    return new CurrentPerformanceBean(currentSpeed, speedCheckFrequency, stratMonitoringTimePrev,
        endMonitoringTime);
  }

  @Override
  public void dumpTweetWithError(Status tweet, Exception exception) {
    if (dumpErrorTweets) {
      dumpTweetToFile(tweet, true);
      dumpError(tweet, exception);
    }
  }

  protected void dumpTweetToFile(Status tweet, boolean ignoreFrequency) {


    if (ignoreFrequency || (dumpFrequency > 0 && (counter % dumpFrequency) == 0)) {
      LOGGER.info("dump tweet#" + counter);

      File dir = new File(dumpDirectoryPath);
      File tweetFile = new File(dir, "tw" + tweet.getId() + ".json");

      try {
        FileUtils.write(tweetFile, DataObjectFactory.getRawJSON(tweet), "UTF-8");

      } catch (FileNotFoundException e) {
        LOGGER.error("Dump tweet failed", e);
      } catch (IOException e) {
        LOGGER.error("Dump tweet failed", e);
      }

    }

  }

  protected void dumpError(Status tweet, Exception exception) {

    LOGGER.info("dump error " + exception.getClass());

    File dir = new File(dumpDirectoryPath);
    File tweetFile = new File(dir, "tw" + tweet.getId() + ".error");

    try {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      exception.printStackTrace(pw);
      pw.flush();
      sw.flush();
      FileUtils.write(tweetFile, sw.toString(), "UTF-8");

    } catch (FileNotFoundException e) {
      LOGGER.error("Dump error failed", e);
    } catch (IOException e) {
      LOGGER.error("Dump error failed", e);
    }

  }

  /**
   * Counter started with 1 (for calculation reason)
   */
  @Override
  public int getProccededTweetsCount() {
    return counter - 1;
  }

  @Override
  public int getUsefullTweetsCount() {
    return usefullCounter;
  }

  @Override
  public void incUsefullTweetsCount() {
    usefullCounter++;
  }

  @Override
  public boolean canProcessTrend() {
    return counter % trendFrequency == 0;
  }
}
