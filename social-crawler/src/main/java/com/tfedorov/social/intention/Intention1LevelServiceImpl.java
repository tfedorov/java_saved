package com.tfedorov.social.intention;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfedorov.social.intention.dao.IntentLexiconDao;
import com.tfedorov.social.intention.util.PatternBuilder;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("intention1LevelServiceImpl")
public class Intention1LevelServiceImpl implements Intention1LevelService {

  private static final String SPACE = " ";

  public static final String TWITTER_OR_OPERATOR = " OR ";

  public static final int TWEETS_DB_PAGE_SIZE = 1000;

  private volatile List<IntentLexicon> intentLexicons;

  private volatile Map<Integer, LexiconBean> lexiconMap;

  private volatile Map<String, Pattern> patternQualifMap = new HashMap<String, Pattern>();

  private volatile Set<String> intentSearchTerms;

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  @Autowired
  private IntentLexiconDao intentLexiconDaoImpl;

  @Override
  public void reload() {
    initLexicons();
  }

  @Override
  public void add(List<IntentLexicon> intentLexicons) {
    intentLexiconDaoImpl.insertIntentLexicons(intentLexicons);
  }

  @Override
  public void save(List<IntentLexicon> intentLexicons) {
    add(intentLexicons);
    reload();
  }

  @PostConstruct
  private void initLexicons() {
    EtmPoint perfPoint = getPerformancePoint(".initLexicons()");
    try {
      List<IntentLexicon> lexicons = intentLexiconDaoImpl.getIntentLexicons();
      Set<String> intentTerms = new TreeSet<String>();
      Map<String, Pattern> patternQualifMapTemp = new HashMap<String, Pattern>();

      Map<Integer, LexiconBean> lexiconMapLocal =
          new HashMap<Integer, Intention1LevelServiceImpl.LexiconBean>();
      for (IntentLexicon lexicon : lexicons) {
        lexiconMapLocal.put(lexicon.hashCode(), new LexiconBean(lexicon));
        intentTerms.add(lexicon.getSearchTerm());
        for (Qualification qualification : lexicon.getQualifications()) {
          String qualificationStr = qualification.getQualificationStr();
          patternQualifMapTemp.put(qualificationStr,
              PatternBuilder.buildForSingleWord(qualificationStr));
        }
      }

      this.lexiconMap = Collections.unmodifiableMap(lexiconMapLocal);
      this.intentLexicons = Collections.unmodifiableList(lexicons);
      this.intentSearchTerms = Collections.unmodifiableSet(intentTerms);
      this.patternQualifMap = Collections.unmodifiableMap(patternQualifMapTemp);
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public List<IntentLexicon> getIntentLexicons() {
    return intentLexicons;
  }

  @Override
  public Set<String> getIntentSearchTerms() {
    return intentSearchTerms;
  }


  @Override
  public boolean isIntention(String text) {
    EtmPoint performancePoint = getPerformancePoint(".isIntention()");

    boolean result = false;
    try {
      for (IntentLexicon intentLexicon : intentLexicons) {
        boolean isIntention = isMutchForTerm(text, intentLexicon.hashCode());
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

  @Override
  public IntentString isIntentionString(String normalizedText, String originalText) {
    EtmPoint performancePoint = getPerformancePoint(".isIntentionString()");

    IntentString is = new IntentString();

    boolean result = false;

    try {
      for (IntentLexicon intentLexicon : intentLexicons) {
    	  if(intentLexicon.getSearchTerm().contains("conside")){
    		System.out.println(intentLexicon.getSearchTerm());  
    	  }
        boolean isIntention = isMutchForTerm(normalizedText, intentLexicon.hashCode());
        if (isIntention) {
          originalText = highlightTerm(originalText, intentLexicon);
          for (Qualification qualification : intentLexicon.getQualifications()) {
            originalText =
                highlightQualification(originalText, qualification.getQualificationStr());
          }
        }
        result = result | isIntention;
        if (result) {
          break;
        }
      }

      is.setIntention(result);
      is.setResultHTML(originalText);

      return is;
    } finally {
      performancePoint.collect();
    }

  }

  private String highlightQualification(String text, String qualificationStr) {

    String coloredQualification =
        new StringBuilder(" <span class=\"intent_qualification\"> ").append(qualificationStr)
            .append(" </span> ").toString();

    Pattern pattern = patternQualifMap.get(qualificationStr);
    Matcher matcher = pattern.matcher(text);

    if (matcher.find() && !text.contains(coloredQualification)) {
      text = matcher.replaceAll(coloredQualification);
    }
    return text;
  }

  private String highlightTerm(String text, IntentLexicon intentLexicon) {

    String colredTerm =
        new StringBuilder(" <span class=\"intent_term\"> ").append(intentLexicon.getSearchTerm())
            .append(" </span> ").toString();

    Pattern pattern = lexiconMap.get(intentLexicon.hashCode()).patternTerm;
    Matcher matcher = pattern.matcher(text);

    if (matcher.find() && !text.contains(colredTerm)) {
      text = matcher.replaceAll(colredTerm);
    } else {

      // This is Crutch for case text " need, "
      // AXS-76
      // add problels to the search temrs
      text =
          text.replaceAll(intentLexicon.getSearchTerm(), SPACE + intentLexicon.getSearchTerm() + SPACE);
      matcher = pattern.matcher(text);
      text = matcher.replaceAll(colredTerm);
    }
    
    return text;
  }

  private boolean isMutchForTerm(String text, int lexiconHash) {
    Pattern pattern = lexiconMap.get(lexiconHash).patternTermAndQualif;
    Matcher matcher = pattern.matcher(text);
    return matcher.find();
  }


  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(Intention1LevelServiceImpl.class
        .toString()).append(name).toString());
  }

  protected void setIntentLexiconDaoImpl(IntentLexiconDao intentLexiconDaoImpl) {
    this.intentLexiconDaoImpl = intentLexiconDaoImpl;
  }

  private class LexiconBean {
    private Pattern patternTermAndQualif;
    private Pattern patternTerm;

    public LexiconBean(IntentLexicon lexiconFromDB) {
      String term = lexiconFromDB.getSearchTerm();
      this.patternTermAndQualif =
          PatternBuilder.buildTermAndQualifs(term, lexiconFromDB.getQualifications());
      this.patternTerm = PatternBuilder.buildForSingleWord(term);

    }

  }

  @Override
  public List<String> getLexiconLanguage() {
    return intentLexiconDaoImpl.getLexiconLanguages();
  }
}
