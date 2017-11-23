package com.tfedorov.social.twitter.processing.filtering;

import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.twitter.processing.tweet.TweetProcessingContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tfedorov.social.twitter.processing.GeneralProcessingContext;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

public class PrintBlackListedTweetTerminalHandler
    implements
        ProcessingHandler<GeneralProcessingContext> {

  private Logger logger = LoggerFactory.getLogger(PrintBlackListedTweetTerminalHandler.class);

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  @Override
  public void process(GeneralProcessingContext context) {
    EtmPoint perfPoint =
        performanceMonitor.createPoint(PrintBlackListedTweetTerminalHandler.class
            + ".processImpl()");

    try {
      TweetProcessingContext twContext = context.getTweetContext();
      String blackWord = (String) twContext.get(TweetProcessingContext.BLACK_WORD_FOUND);
      if (blackWord != null) {
        logger.info("Black word:[" + blackWord + "] in tweet:"
            + twContext.getTweetInfo().getTweet().toString());
      }
    } finally {

      perfPoint.collect();
    }
  }

  @Override
  public Class<?> getClazz() {
    return PrintBlackListedTweetTerminalHandler.class;
  }

  @Override
  public JSONObject returnJson() throws JSONException {
    JSONObject point = new JSONObject();
    point.put("cname", this.getClass().getName());
    return point;
  }
}
