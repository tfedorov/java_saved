/**
 * 
 */
package com.tfedorov.social.twitter.sentiments.strategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import com.tfedorov.social.intention.Qualification;
import com.tfedorov.social.twitter.processing.sentiments.util.SentimentLexicon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfedorov.social.intention.util.PatternBuilder;
import com.tfedorov.social.qualification.util.QuailficationUtil;
import com.tfedorov.social.twitter.sentiments.SENTIMENT;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * @author tfedorov
 * 
 */
@Service(value = "qualifSentimentStrategy")
public class QualifSentimentStrategy implements SentimentStrategy {

  private Logger logger = LoggerFactory.getLogger(QualifSentimentStrategy.class);
  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  // According to https://datamartllc.atlassian.net/browse/AXS-104
  private static final String QUALIFICATIONS_STR = "&2.0 not,&2.0 nor,&2.0 none,&2.0 n't";

  @Autowired
  private SentimentDAO sentimentDAO;

  private volatile Map<SentimentLexicon, Pattern> lexiconMap;

  private volatile Set<String> languages = new HashSet<String>();

  private List<Qualification> qualifications;

  @PostConstruct
  public void init() {
    EtmPoint etmPoint = getPerformancePoint(".init()");
    try {
      qualifications = QuailficationUtil.buildQuaificationStr(QUALIFICATIONS_STR);
      List<SentimentLexicon> lexicons = sentimentDAO.getSentimentLexicons();
      Map<SentimentLexicon, Pattern> lexiconPatternMap =
          new HashMap<SentimentLexicon, Pattern>(lexicons.size() * 2);
      for (SentimentLexicon sentimentLexicon : lexicons) {

        Pattern pattern = null;
        if (qualifications != null && !qualifications.isEmpty()) {
          pattern =
              PatternBuilder.buildTermAndQualifs(sentimentLexicon.getSearchTerm(), qualifications);
        }
        lexiconPatternMap.put(sentimentLexicon, pattern);
      }

      this.lexiconMap = Collections.unmodifiableMap(lexiconPatternMap);
      this.languages = new HashSet<String>(sentimentDAO.loadSupportedLanguagesList());
    } finally {
      etmPoint.collect();
    }
  }

  @Override
  public SENTIMENT provideSentiment(List<String> wordsList, String tweetText, String lang) {

    // If tweet languages different from languages supported by sentiment analys
    if (!languages.contains(lang)) {
      return SENTIMENT.neutral;
    }

    int sentimentCount = getSentimentMark(wordsList, tweetText);

    if (sentimentCount == 0) {
      return SENTIMENT.neutral;
    }

    return sentimentCount > 0 ? SENTIMENT.positive : SENTIMENT.negative;
  }


  private int getSentimentMark(List<String> wordsList, String tweetText) {
    int sentimentCount = 0;
    for (Entry<SentimentLexicon, Pattern> sentimentLexicon : lexiconMap.entrySet()) {
      String searchTerm = sentimentLexicon.getKey().getSearchTerm();
      if (wordsList.contains(searchTerm)) {
        Pattern pattern = sentimentLexicon.getValue();
        if (pattern != null) {
          Matcher matcher = pattern.matcher(tweetText);
          if (matcher.find()) {
            // if qualifier word (not, nor, doesn't) appear befor sentiment word change sentiment
            // from positiv to negativ or vice versa
            sentimentCount =
                sentimentLexicon.getKey().isPositiv() ? sentimentCount - 1 : sentimentCount + 1;
          } else {
            // if qualifier word (not, nor, doesn't) appear befor sentiment word change sentiment
            // from positiv to negativ or vice versa
            sentimentCount =
                sentimentLexicon.getKey().isPositiv() ? sentimentCount + 1 : sentimentCount - 1;
          }
          logger.trace(" mark =[" + sentimentCount + "], term = [" + searchTerm + "] , tweet ["
              + tweetText + "]");
          // qualification column is empty for this SENTIMENT
        } else {
          sentimentCount =
              sentimentLexicon.getKey().isPositiv() ? sentimentCount + 1 : sentimentCount - 1;
        }
      }
    }
    return sentimentCount;
  }

  @Override
  public void reloadWords() {
    init();
  }

  public void setSentimentDAO(SentimentDAO sentimentDAO) {
    this.sentimentDAO = sentimentDAO;
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(QualifSentimentStrategy.class
        .toString()).append(name).toString());
  }

}
