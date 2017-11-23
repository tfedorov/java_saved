package com.tfedorov.social.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tfedorov.social.normalization.stemming.StemmingService;
import com.tfedorov.social.twitter.streaming.TwitterStreamingService;
import com.tfedorov.social.twitter.streaming.performance.TwitterStreamingHealthService;
import com.tfedorov.social.utils.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.tfedorov.social.cleaner.SocialCleanerService;
import com.tfedorov.social.intention.IntentionService;
import com.tfedorov.social.twitter.aggregation.TweetsAggregationService;
import com.tfedorov.social.twitter.sentiments.strategy.SentimentStrategy;
import com.tfedorov.social.twitter.tracing.CurrentPerformanceBean;
import com.tfedorov.social.twitter.tracing.TweetTracingService;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;

public class CrawlerManagerServlet extends HttpServlet {

  private static final String END_H4 = " ]</h4>";
  private static final String START_H4 = "<h4>[";
  private static final int MEMORY_SIZE = 1024;
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
  private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerManagerServlet.class);

  @Autowired
  private TwitterStreamingService twitterStreamingService;
  @Autowired
  private TwitterStreamingHealthService twitterStreamingHealthService;
  @Autowired
  private IntentionService intentionService;
  @Autowired
  private TweetTracingService tracingService;
  @Autowired
  private StemmingService stemmingService;
  @Autowired
  private SentimentStrategy sentimentStrategy;
  @Autowired
  private SocialCleanerService socialCleanerService;
  @Autowired
  private TweetsAggregationService tweetsAggregationService;

  private EtmMonitor pMonitor;

  /**
   *
   */
  private static final long serialVersionUID = -5382029014175733971L;

  @Override
  public void init(ServletConfig config) throws ServletException {
    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
        config.getServletContext());
    super.init(config);
    // ApplicationContext applicationContext =
    // WebApplicationContextUtils.getWebApplicationContext(getServletContext());
    // twitterStreamingService =
    // applicationContext.getBean("twitterStreamingService",
    // TwitterStreamingServiceImpl.class);
    pMonitor = EtmManager.getEtmMonitor();
    LOGGER.info("[Init finished]");
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("text/html");

    PrintWriter writer = response.getWriter();
    writer.write("<h2>[ Management Servlet ]</h2>");

    if (request.getParameter("crawler") != null) {
      if (request.getParameter("crawler").equalsIgnoreCase("start")) {
        twitterStreamingService.startCrawler();
        writer.write("<h3>Twitter Streaming Service Started!</h3>");
      } else if (request.getParameter("crawler").equalsIgnoreCase("stop")) {
        // twitterStreamingService.shutDownCrawler();
        // TODO: check if properly working
        twitterStreamingService.stopCrawler();
        writer.write("<h3>Twitter Streaming Service Stopped!</h3>");
      }
    } else if (request.getParameter("reload") != null) {
      if (request.getParameter("reload").equalsIgnoreCase("word")) {
        twitterStreamingService.reloadWords();
        writer.write("<h3>Twitter Streaming Service - Words(Stop/Black) reloaded!</h3>");
      } else if (request.getParameter("reload").equalsIgnoreCase("topic")) {
        twitterStreamingService.reloadTopics();
        writer.write("<h3>Twitter Streaming Service - Topics reloaded!</h3>");
      } else if (request.getParameter("reload").equalsIgnoreCase("intent")) {
        intentionService.reloadIntentions();
        writer.write("<h3>Intention service - Intents is reloaded!</h3>");
      } else if (request.getParameter("reload").equalsIgnoreCase("sentiments")) {
        sentimentStrategy.reloadWords();
        writer.write("<h3>Sentiments service - sentiments is reloaded!</h3>");
      }
    } else if (request.getParameter("update") != null) {
      if (request.getParameter("update").equalsIgnoreCase("topic")) {
        twitterStreamingService.updateTopics();
        writer.write("<h3>Twitter Streaming Service - Topics updated!</h3>");
      }
    } else if (request.getParameter("pmon") != null) {
      if (request.getParameter("pmon").equalsIgnoreCase("start")) {
        pMonitor.start();
        writer.write("<h3>Perfomance monitoring started!</h3>");
      } else if (request.getParameter("pmon").equalsIgnoreCase("stop")) {
        pMonitor.stop();
        writer.write("<h3>Perfomance monitoring stopped!</h3>");
      }

    } else if (request.getParameter("system") != null) {
      if (request.getParameter("system").equalsIgnoreCase("gc")) {
        System.gc();
        writer.write("<h3>Force GC!</h3>");
      }
    } else if (request.getParameter("health") != null) {
      if (request.getParameter("health").equalsIgnoreCase("start")) {
        twitterStreamingHealthService.start();
        writer.write("<h3>Twitter Streamint Helth Service Started!</h3>");
      } else if (request.getParameter("health").equalsIgnoreCase("stop")) {
        twitterStreamingHealthService.stop();
        writer.write("<h3>Twitter Streamint Helth Service Stoped!</h3>");
      }
    } else if (request.getParameter("stemming") != null) {
      if (request.getParameter("stemming").equalsIgnoreCase("start")) {
        stemmingService.enableUpdateHistory();
      }
      if (request.getParameter("stemming").equalsIgnoreCase("stop")) {
        stemmingService.disableUpdateHistory();
      }
      if (request.getParameter("stemming").equalsIgnoreCase("clear")) {
        stemmingService.cleanUselessSteam();
      }
    } else if (request.getParameter("latest") != null) {
      if (request.getParameter("latest").equalsIgnoreCase("clean")) {
        writer.write("<h3>This functionality has been dprecated & removed!</h3>");
      }
    } else if (request.getParameter("statistic") != null) {
      if (request.getParameter("statistic").equalsIgnoreCase("clean")) {
        socialCleanerService.cleanTweetStatistics();
      }
    } else if (request.getParameter("chain") != null) {
      if (request.getParameter("chain").equalsIgnoreCase("show")) {
        writer.write("<h4>[Chain structure:]</h4>");
        writer.write(tweetsAggregationService.getChainStructure().toString());
      }
    }

    writer.write("<h3>[ TSS active=" + twitterStreamingService.isActive() + " ]</h3>");

    writer.write("<h3>[ TSHS active=" + twitterStreamingHealthService.isActive() + " ]</h3>");
    writer.write("<h3>[  perfomance monitoring started=" + pMonitor.isStarted() + ", collecting="
        + pMonitor.isCollecting() + "  ]</h3>");

    writer.write("<h3>[ Tracked topics count = " + twitterStreamingService.getTopicsNumber()
        + " ]</h3>");

    long mbFree = Runtime.getRuntime().freeMemory() / (MEMORY_SIZE * MEMORY_SIZE);
    long mbTotal = Runtime.getRuntime().totalMemory() / (MEMORY_SIZE * MEMORY_SIZE);
    long mbMax = Runtime.getRuntime().maxMemory() / (MEMORY_SIZE * MEMORY_SIZE);
    double pFree = (double) mbFree / (double) mbTotal;

    Object[] mArgFM = {pFree * 100};

    String freePerc = MessageFormat.format("({0,number,integer}%)", mArgFM);
    if (pFree < 0.2) {
      freePerc =
          MessageFormat.format("<font color=\"red\"> ({0,number,integer}% !!!) </font>", mArgFM);
    } else if (pFree < 0.3) {
      freePerc =
          MessageFormat.format("<font color=\"orange\"> ({0,number,integer}% !!!) </font>", mArgFM);
    }

    Object[] mArg = {mbFree, freePerc, mbTotal, mbMax};
    writer.write("<h4>[Memmory usage: free(%)/total/max = "
        + MessageFormat.format(
            "{0,number,integer}Mb {1} / {2,number,integer}Mb / {3,number,integer}Mb", mArg)
        + "]</h4>");

    writer.write(START_H4 + createTweetSizeStr() + END_H4);

    writer.write(printProccededTweetsCount());

    writer.write("<h4>[Current tweets processing speed" + curFrequencyText() + END_H4);

    writer.write("<h4>[Stemming history : "
        + (stemmingService.isStemmingHistoryEnabled() ? " active" : " disactive ")
        + " , queu size = " + stemmingService.getQueueSize() + END_H4);

    writer.write("<h4>[Run Info]</h4>");
    writer.write(twitterStreamingService.getRunInfo());

    writer.flush();


  }

  private String curFrequencyText() {
    CurrentPerformanceBean curPerformance = tracingService.getCurPerformance();
    double currentSpeed = curPerformance.getCurrentSpeed();
    StringBuilder result = new StringBuilder();
    if (currentSpeed != 0) {
      result.append(" = ").append(DECIMAL_FORMAT.format(currentSpeed)).append(" milisec / tweet");
    } else {
      result.append(" calculating ...");
    }
    result.append(" <- number/[from]-[to] = ").append(curPerformance.getProcessedNumeber())
        .append(" tweet ");
    result.append(" / [").append(printDate(curPerformance.getStrartMonitoringMS())).append("]");
    result.append(" - [").append(printDate(curPerformance.getEndMonitoringMS())).append(" ] ");

    return result.toString();
  }

  private String printDate(Long timeInMs) {
    String result = "";
    if (timeInMs != null) {
      result = DateUtils.printDateMiliSecond(timeInMs);
    } else {
      result = " <i> undefined </i> ";
    }
    return result;
  }

  private String printProccededTweetsCount() {
    int proccededTweetsCount = tracingService.getProccededTweetsCount();
    int usefullTweetsCount = tracingService.getUsefullTweetsCount();
    String percentageVal = "0";
    if (proccededTweetsCount > 0) {
      float percentage = (float) usefullTweetsCount / proccededTweetsCount * 100;
      percentageVal = DECIMAL_FORMAT.format(percentage);
    }
    StringBuilder result =
        new StringBuilder(START_H4).append("Processed tweets: useful(%)/all = ")
            .append(tracingService.getUsefullTweetsCount()).append("(").append(percentageVal)
            .append("%)/").append(tracingService.getProccededTweetsCount()).append(END_H4);
    return result.toString();
  }

  private String createTweetSizeStr() {

    int streamingSize = twitterStreamingService.getTweetsQueueSize();
    if (streamingSize == Integer.MAX_VALUE) {
      return "Error in displaying streaming size";
    }
    if (streamingSize < 0) {
      return "Tweets queue size doesn't initialised";
    }
    StringBuilder result = new StringBuilder("Tweets queue size = ");
    if (streamingSize < 1000) {
      return result.append(streamingSize).toString();
    }
    if (streamingSize > 10000) {
      return result.append("<font color=\"red\">").append(streamingSize).append("</font>")
          .toString();
    }

    return result.append("<font color=\"orange\">").append(streamingSize).append("</font>")
        .toString();

  }
}
