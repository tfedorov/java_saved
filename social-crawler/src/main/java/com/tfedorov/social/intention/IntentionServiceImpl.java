package com.tfedorov.social.intention;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("intentionServiceImpl")
public class IntentionServiceImpl implements IntentionService {

  @Autowired
  private Intention1LevelService intention1LevelServiceImpl;

  @Autowired
  private Intention2LevelService intention2LevelServiceImpl;

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private volatile Set<String> lexiconLanguage;

  private volatile Set<String> allIntentTerms;

  @PostConstruct
  private void initialize() {
    Set<String> lexLang = new HashSet<String>();
    lexLang.addAll(intention1LevelServiceImpl.getLexiconLanguage());
    lexLang.addAll(intention2LevelServiceImpl.getLexiconLanguage());
    lexiconLanguage = Collections.unmodifiableSet(lexLang);
    // init all intent terms list
    Set<String> allIntentsTerms = new HashSet<String>();
    allIntentsTerms.addAll(intention1LevelServiceImpl.getIntentSearchTerms());
    allIntentsTerms.addAll(intention2LevelServiceImpl.getPrimaryTests());
    allIntentTerms = Collections.unmodifiableSet(allIntentsTerms);
  }

  @Override
  public boolean isIntention(String text) {
    EtmPoint perfPoint = getPerformancePoint(".isIntention()");
    try {
      return intention2LevelServiceImpl.isIntention(text)
          && intention1LevelServiceImpl.isIntention(text);
    } finally {
      perfPoint.collect();
    }

  }

  @Override
  public IntentString isIntentionString(String normalizedText, String originalText) {
    EtmPoint perfPoint = getPerformancePoint(".isIntentionString()");
    try {
      IntentString intentString = new IntentString();
      if (intention2LevelServiceImpl.isIntention(normalizedText)) {
        intentString = intention1LevelServiceImpl.isIntentionString(normalizedText, originalText);
      }
      return intentString;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public Set<String> getAllIntentTrms() {
    return allIntentTerms;
  }

  @Override
  public void reloadIntentions() {
    EtmPoint perfPoint = getPerformancePoint(".reloadIntentions()");
    try {
      intention1LevelServiceImpl.reload();
      intention2LevelServiceImpl.reload();
      initialize();
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public boolean isLanguageExist(String lang) {
    return lexiconLanguage.contains(lang);
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(IntentionServiceImpl.class.toString())
        .append(name).toString());
  }
}
