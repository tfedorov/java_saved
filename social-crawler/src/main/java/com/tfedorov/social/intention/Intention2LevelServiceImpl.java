package com.tfedorov.social.intention;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import com.tfedorov.social.intention.dao.Intention2LevelDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfedorov.social.intention.util.PatternBuilder;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("intention2LevelServiceImpl")
public class Intention2LevelServiceImpl implements Intention2LevelService {

  private static final Logger logger = LoggerFactory.getLogger(Intention2LevelServiceImpl.class);

  private volatile List<Purchase> purchases = new ArrayList<Purchase>();

  private volatile Map<String, List<Pattern>> purchasePattern =
      new HashMap<String, List<Pattern>>();

  private volatile Set<String> primaryTests = new TreeSet<String>();

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  @Autowired
  private Intention2LevelDao intention2LevelDaoImpl;


  @PostConstruct
  private void initPurchases() {
    EtmPoint perfPoint = getPerformancePoint(".initPurchases()");
    try {
      List<Purchase> purchaseList = intention2LevelDaoImpl.getLevel2Lexicons();
      Map<String, List<Pattern>> purchasePatternMap = new HashMap<String, List<Pattern>>();
      Set<String> primaryTestsSet = new TreeSet<String>();
      for (Purchase purchase : purchaseList) {
        primaryTestsSet.add(purchase.getPrimaryTest());

        List<Pattern> patterns = PatternBuilder.buildPurchasesPattern(purchase);
        purchasePatternMap.put(purchase.toString(), patterns);
      }
      this.purchases = Collections.unmodifiableList(purchaseList);
      this.primaryTests = Collections.unmodifiableSet(primaryTestsSet);
      this.purchasePattern = Collections.unmodifiableMap(purchasePatternMap);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<Purchase> getPurchases() {
    return purchases;
  }

  /**
   * @return the primaryTests
   */
  @Override
  public Set<String> getPrimaryTests() {
    return primaryTests;
  }

  @Override
  public void reload() {
    initPurchases();
  }

  @Override
  public void add(List<Purchase> intentLexicons) {
    intention2LevelDaoImpl.insertLevel2Lexicons(intentLexicons);
  }

  @Override
  public void save(List<Purchase> intentLexicons) {
    add(intentLexicons);
    reload();
  }

  @Override
  public boolean isIntention(String text) {
    EtmPoint performancePoint = getPerformancePoint(".isIntention()");

    boolean result = false;
    try {
      for (Purchase purchase : purchases) {
        boolean isIntention = isMutchForTerm(text, purchase);
        result = result | isIntention;
        if (result) {
          break;
        }
      }
      return result;
    } finally {
      performancePoint.collect();
    }
  }

  private boolean isMutchForTerm(String text, Purchase purchase) {
    boolean result = true;
    List<Pattern> patterns = purchasePattern.get(purchase.toString());
    for (Pattern pattern : patterns) {
      Matcher matcher = pattern.matcher(text);
      result = result & matcher.find();
    }
    return result;
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(Intention2LevelServiceImpl.class
        .toString()).append(name).toString());
  }

  public void setIntention2LevelDaoImpl(Intention2LevelDao intention2LevelDaoImpl) {
    this.intention2LevelDaoImpl = intention2LevelDaoImpl;
  }

  @Override
  public List<String> getLexiconLanguage() {
    return intention2LevelDaoImpl.getLexiconLanguages();
  }
}
