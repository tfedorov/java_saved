package com.tfedorov.social.twitter.sentiments.strategy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfedorov.social.twitter.sentiments.SENTIMENT;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

//@Service(value = "simpleSentimentStrategy")
@Deprecated
public class SimpleSentimentStrategy implements SentimentStrategy {

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private Set<String> positiveWords;
  private Set<String> negativeWords;
  private Set<String> supportedLanguages;

  @Autowired
  private SentimentDAO sentimentDAO;

  @PostConstruct
  private void init() {
    EtmPoint etmPoint = getPerformancePoint(".init()");
    try {
      positiveWords =
          Collections
              .unmodifiableSet(new HashSet<String>(sentimentDAO.loadPositiveSentimentsList()));
      negativeWords =
          Collections
              .unmodifiableSet(new HashSet<String>(sentimentDAO.loadNegativeSentimentsList()));
      supportedLanguages =
          Collections
              .unmodifiableSet(new HashSet<String>(sentimentDAO.loadSupportedLanguagesList()));
    } finally {
      etmPoint.collect();
    }

  }

  @Override
  public SENTIMENT provideSentiment(List<String> wordsList, String tweetText, String lang) {
    EtmPoint etmPoint = getPerformancePoint(".provideSentiment()");
    try {
      if (supportedLanguages.contains(lang)) {
        int countPositiveWords = 0;
        int countNegativeWords = 0;

        for (String word : wordsList) {
          if (getPositiveWords().contains(word)) {// || getStemmedPositiveWords().contains(word)) {
            countPositiveWords++;
          }
          if (getNegativeWords().contains(word)) {// || getStemmedNegativeWords().contains(word)) {
            countNegativeWords++;
          }
        }
        int i = countPositiveWords - countNegativeWords;
        if (i > 0) {
          return SENTIMENT.positive;
        } else if (i < 0) {
          return SENTIMENT.negative;
        }
      }
      return SENTIMENT.neutral;
    } finally {
      etmPoint.collect();
    }
  }

  @Override
  public void reloadWords() {
    init();
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(SimpleSentimentStrategy.class
        .toString()).append(name).toString());
  }

  public Set<String> getPositiveWords() {
    return positiveWords;
  }

  public Set<String> getNegativeWords() {
    return negativeWords;
  }
}
