package com.tfedorov.social.twitter.emulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.tfedorov.social.twitter.streaming.TwitterStreamingService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.internal.json.DataObjectFactoryUtil;
import twitter4j.json.DataObjectFactory;

import com.tfedorov.social.twitter.aggregation.TweetsAggregationService;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service
public class TwitterStreamingEmulationServiceImpl implements TwitterStreamingEmulationService {

  private static Logger logger = LoggerFactory
      .getLogger(TwitterStreamingEmulationServiceImpl.class);


  @Autowired
  private TweetsAggregationService aggregationService;

  @Autowired
  private TwitterStreamingService streamingService;

  private String inputDirectoryPath = "/opt/dmp-tweets/input";

  private EtmMonitor performanceMonitor;

  @Autowired
  public TwitterStreamingEmulationServiceImpl(
      @Value("${twitter.trace.input.dir:/opt/dmp-tweets/input}") String inputDirectoryPath) {

    this.inputDirectoryPath = inputDirectoryPath;
    performanceMonitor = EtmManager.getEtmMonitor();
  }

  @Override
  public int sendTweets(int tweetsLimit) {
    EtmPoint perfPoint =
        performanceMonitor
            .createPoint(TwitterStreamingEmulationServiceImpl.class + ".sendTweets()");

    int tweetsProcessed = 0;
    try {

      if (streamingService.isActive()) {
        logger
            .warn("Tweets sending emulation isn't allowed when TwitterStreamingService is active");
      } else {

        File inputDir = new File(inputDirectoryPath);

        Collection<File> files = FileUtils.listFiles(inputDir, new String[] {"json"}, false);

        for (File file : files) {

          if (tweetsProcessed >= tweetsLimit) {
            break;
          }

          logger.info("Input tweet:" + file.getAbsolutePath());

          try {

            String rawJson = FileUtils.readFileToString(file, "UTF-8");

            Status status = DataObjectFactory.createStatus(rawJson);
            DataObjectFactoryUtil.registerJSONObject(status, rawJson);

            aggregationService.processStatus(status);

            file.renameTo(new File(file.getCanonicalPath() + ".sent"));
            tweetsProcessed++;

          } catch (FileNotFoundException e) {
            logger.error("Load tweet failed", e);
          } catch (UnsupportedEncodingException e) {
            logger.error("Load tweet failed", e);
          } catch (IOException e) {
            logger.error("Load tweet failed", e);
          } catch (TwitterException e) {
            logger.error("Load tweet failed", e);
          } finally {
            DataObjectFactoryUtil.clearThreadLocalMap();
          }

        }

      }

    } finally {

      perfPoint.collect();
    }

    return tweetsProcessed;
  }

  @Override
  public String sendTweet(String jsonTweet) {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(TwitterStreamingEmulationServiceImpl.class + ".sendTweet()");

    try {
      if (streamingService.isActive()) {
        logger
            .warn("Tweets sending emulation isn't allowed when TwitterStreamingService is active");
      } else {
        try {
          Status status = DataObjectFactory.createStatus(jsonTweet);
          DataObjectFactoryUtil.registerJSONObject(status, jsonTweet);
          aggregationService.processStatus(status);
        } catch (TwitterException e) {
          logger.error("Tweet creation failed: ", e);

        } finally {
          DataObjectFactoryUtil.clearThreadLocalMap();
        }
        logger.info("Tweet sent to aggregator via EmulationService");
      }

    } finally {
      perfPoint.collect();
    }
    return "Success";
  }
}
