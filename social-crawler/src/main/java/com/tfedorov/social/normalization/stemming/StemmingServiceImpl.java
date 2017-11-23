package com.tfedorov.social.normalization.stemming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.tfedorov.social.normalization.NormalizationProcessor;
import com.tfedorov.social.stemming.dao.StemmingBean;
import com.tfedorov.social.stemming.dao.WordCountBean;
import com.tfedorov.social.utils.date.DateUtils;
import org.apache.lucene.analysis.Analyzer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfedorov.social.stemming.dao.StemmingDAO;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("stemmingService")
public class StemmingServiceImpl implements StemmingService {

  private static final int SLEEP_VALUE = 100;
  private static final int MIN_WORD_LENGTH = 3;
  private static final int MAX_WORD_LENGTH = 50;
  private static final String MARKER_WORD = "markerforstemcleaner";
  private static final String MARKER_STEMMED_WORD = "markerforstemcleanerstemmedword";

  private static final String SPACE = " ";

  private static final NormalizationProcessor PROCESSOR = new NormalizationProcessor();

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();
  private Logger logger = LoggerFactory.getLogger(StemmingServiceImpl.class);

  @Autowired
  private StemmingDAO stemmingDAO;

  private StemmingUpdateThread updaterThread;

  private volatile boolean updateHistoryEnable = true;

  //
  private static final int MAX_ARRAY_SIZE = 1000;
  private StemmingResult[] stemmingResultArray = new StemmingResult[MAX_ARRAY_SIZE];
  private int currentArrayIndex = 0;

  protected void setStemmingDAO(StemmingDAO stemmingDAO) {
    this.stemmingDAO = stemmingDAO;
  }

  @PostConstruct
  protected void init() {
    if (updaterThread == null) {
      updaterThread = new StemmingUpdateThread() {
        @Override
        public void update(StemmingResult[] stemmingResult) {
          EtmPoint perfPoint = getPerformancePoint(".stemmingDBUpdate()");
          try {
            // update on DB
            int[] results = stemmingDAO.updateStemmedWord(stemmingResult);
            //
            Map<StemmingResult, Integer> insertList = new HashMap<StemmingResult, Integer>();
            //
            for (int i = 0; i < results.length; i++) {
              int result = results[i];
              StemmingResult sr = stemmingResult[i];
              if (result == 0) {
                if (insertList.containsKey(sr)) {
                  insertList.put(sr, insertList.get(sr) + 1);
                }
                insertList.put(sr, 1);
              } else {
                if (result > 1) {
                  logger.warn("Found dublication (" + result + ") on updating stemming DB. Word:"
                      + stemmingResult[i].getWord() + " Stemmed word: "
                      + stemmingResult[i].getStemmedWord() + " Language: "
                      + stemmingResult[i].getLanguage());
                }
              }
            }
            if (insertList.size() > 0) {
              stemmingDAO.addNewStemmedWord(insertList);
            }
          } catch (Exception e) {
            logger.error("Exception when try update stemming DB!", e);
            try {
              logger.warn("Stemming updater thread go sleep for 10 seconds!");
              Thread.sleep(StemmingUpdateThread.THREAD_SLEEP_TIME);
            } catch (InterruptedException ex) {
              logger.error("Error on stemming DB update thread!", ex);
            }
          } finally {
            perfPoint.collect();
          }
        }
      };
      updaterThread.start();
    }
  }

  @Override
  public void initializeAnalyzers(Set<String> stopWords) {
    // initialize normalizing analyzers
    PROCESSOR.initializeNormalizingAnalyzers(stopWords);
  }

  @PreDestroy
  protected void destroy() {
    if (updaterThread != null && updaterThread.isWork()) {
      updaterThread.stopWork();
    }
  }

  @Override
  @Deprecated
  public String steamStringLine(String text, String language) {
    EtmPoint perfPoint = getPerformancePoint(".steamStringLine():");
    try {
      if (isLanguageExist(language)) {
        // get analyzer for language
        Analyzer analyzer = getAnalyzer(language);
        // steam and return
        String stemmedWord = PROCESSOR.steamStringLine(text, analyzer);
        
        return stemmedWord;
      }
      return text;
    } catch (Exception e) {
      logger.error("Exception on try stemming text : ' " + text + " ' with language: ' " + language
          + " '!", e);
      return text;
    } finally {
      perfPoint.collect();
    }
  }


  /**
   * THIS METHOD CAN'T BE CALLED FROM PLACE WHERE CAN BE ACCESSED SEVERAL THREAD
   */
  @Override
  @Deprecated
  public List<String> steamStringLineToList(String text, String language) {
    EtmPoint perfPoint = getPerformancePoint(".steamStringLine():");
    try {
      if (isLanguageExist(language)) {
        // get analyzer for language
        Analyzer analyzer = getAnalyzer(language);
        // steam
        List<String> stemmedWordList = PROCESSOR.steamStringLineToArray(text, analyzer);
        // update stemming data base
        sendStemmingResultListToProcess(text, stemmedWordList, language);
        // and return
        return stemmedWordList;
      }
      List<String> result = new ArrayList<String>();
      result.add(text);
      return result;
    } catch (Exception e) {
      logger.error("Exception on try stemming text : ' " + text + " ' with language: ' " + language
          + " '!", e);
      List<String> r = new ArrayList<String>();
      r.add(text);
      return r;
    } finally {
      perfPoint.collect();
    }
  }

  public void cleanUselessSteam() {

    EtmPoint perfPoint = getPerformancePoint(".cleanUselessSteam()");
    // restore updateHistory state after finishing of cleaning
    boolean updateHistoryBeforeCleaning = updateHistoryEnable;
    // Stop updating History in while cleaning
    if (updateHistoryEnable) {
      updateHistoryEnable = false;
    }
    try {
      DateTime endCleaningDate = DateUtils.getCurrentDateTime();
      StemmingResult markedReccord = getMarkeredRecord();
      DateTime startCleaningDate = getStartCleaningDate(markedReccord);

      int numberOfDeletedRec = 0;

      List<String> langList = stemmingDAO.getStemmingLangsByCount();
      // Do cleaning by language first language should by English
      for (String lang : langList) {
        do {
          // Do UNIQU cleaning by portion 1000 rows
          numberOfDeletedRec =
              stemmingDAO.cleanUselessSteamLimited(startCleaningDate, endCleaningDate, lang);
          logger
              .warn("Stem cleaner delete " + numberOfDeletedRec + " records for language " + lang);
          // Do TimeOut because another application threads should also work :-)
          try {
            Thread.sleep(SLEEP_VALUE);
          } catch (InterruptedException e) {
            logger.error("Exception within stemm cleaner", e);
            break;
          }
        } while (numberOfDeletedRec > 0);

      }

      saveMarker(markedReccord, endCleaningDate);
      logger.warn("Stemm cleaner checke all steems before " + endCleaningDate);

    } finally {
      perfPoint.collect();
      // restore updateHistory state after finishing of cleaning
      updateHistoryEnable = updateHistoryBeforeCleaning;
    }
  }

  private DateTime getStartCleaningDate(StemmingResult markedReccord) {
    DateTime startOfCleaning;

    if (markedReccord != null && markedReccord.getModificationDate() != null) {
      startOfCleaning = markedReccord.getModificationDate();
    } else {
      // in the case of the first cleaning
      // marker field doesn't exist before
      startOfCleaning = DateUtils.getIntervalToToday(60).getStart();
    }
    return startOfCleaning;
  }

  private void saveMarker(StemmingResult markedReccord, DateTime endOfCleaning) {
    StemmingResult[] stemmingArrayForSaving = new StemmingResult[1];
    if (markedReccord != null) {
      markedReccord.setModificationDate(endOfCleaning);
      stemmingArrayForSaving[0] = markedReccord;
      stemmingDAO.updateStemmedWord(stemmingArrayForSaving);
    } else {
      // in the case of the first cleaning
      // marker field doesn't exist before

      markedReccord = new StemmingResult(MARKER_WORD, MARKER_STEMMED_WORD, "fake", endOfCleaning);
      final Map<StemmingResult, Integer> stemmingListForSaving =
          new HashMap<StemmingResult, Integer>(1);
      stemmingListForSaving.put(markedReccord, 1);
      stemmingDAO.addNewStemmedWord(stemmingListForSaving);
    }
  }

  private StemmingResult getMarkeredRecord() {
    List<StemmingResult> markedReccordList = stemmingDAO.getStemmingByWords(MARKER_WORD);

    StemmingResult markedReccord;
    if (markedReccordList != null && markedReccordList.size() > 0) {
      if (markedReccordList.size() > 1) {
        logger
            .error("There is dublicated fields for marked row in stemming table look at the word "
                + MARKER_WORD);
      }
      markedReccord = markedReccordList.get(0);
      if (!MARKER_STEMMED_WORD.equals(markedReccord.getStemmedWord())
          || !"fake".equals(markedReccord.getLanguage())) {
        logger.error("The marker record is not correct ");
      }
      return markedReccord;
    }
    return null;
  }


  private void sendStemmingResultToProcess(String text, String stemmedText, String language) {
    if (updateHistoryEnable) {
      EtmPoint perfPoint = getPerformancePoint(".sendStemmingResultToProcess()");
      try {
        String trimmedText = text.trim();
        // check text for has " " and for empty and more than 3 symbols // and not equals stemmed
        // text
        if (trimmedText.indexOf(SPACE) < 0 && !trimmedText.isEmpty()
            && trimmedText.length() >= MIN_WORD_LENGTH && trimmedText.length() <= MAX_WORD_LENGTH) {
          if (currentArrayIndex == MAX_ARRAY_SIZE) {
            // replace new array instead of old and put old array to processing thread
            processArray();
            // put element to new array
            putStemmingResultToArray(new StemmingResult(trimmedText, stemmedText, language));
          } else {
            putStemmingResultToArray(new StemmingResult(trimmedText, stemmedText, language));
          }
        }
      } finally {
        perfPoint.collect();
      }
    }
  }

  private void processArray() {
    StemmingResult[] temp = stemmingResultArray;
    stemmingResultArray = new StemmingResult[MAX_ARRAY_SIZE];
    currentArrayIndex = 0;
    // put to processing thread
    updaterThread.addToQueue(temp);
  }

  private void putStemmingResultToArray(StemmingResult sr) {
    stemmingResultArray[currentArrayIndex++] = sr;
  }

  private void sendStemmingResultListToProcess(String text, List<String> stemmedTextList,
      String language) {
    for (String stemmedWord : stemmedTextList) {
      sendStemmingResultToProcess(text, stemmedWord, language);
    }
  }

  private Analyzer getAnalyzer(String language) {
    // call factory and return initialized analyzer
    return StemmingAnalyzerFactory.instance().getAnalyzerByLanguage(language);
  }

  private boolean isLanguageExist(String language) {
    return StemmingAnalyzerFactory.instance().getPorterAnalyzerMap().containsKey(language);
  }

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(StemmingServiceImpl.class.toString())
        .append(name).toString());
  }

  @Override
  public void disableUpdateHistory() {
    EtmPoint perfPoint = getPerformancePoint(".disableStemmingUpdateHistory()");
    try {
      updateHistoryEnable = false;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public void enableUpdateHistory() {
    EtmPoint perfPoint = getPerformancePoint(".enableStemmingUpdateHistory()");
    try {
      updateHistoryEnable = true;
    } finally {
      perfPoint.collect();
    }
  }

  @Override
  public boolean isStemmingHistoryEnabled() {
    return updateHistoryEnable;
  }

  @Override
  public Integer getQueueSize() {
    return updaterThread.getQueueSize();
  }

	@Override
	public Map<String, String> loadTopWordsListByStemmed(List<String> stemmedWordsList, final String language) {

		EtmPoint serviceMethodPoint = getPerformancePoint("loadTopWordsListByStemmed()");
		try {
			if (stemmedWordsList.isEmpty()) {
				return new HashMap<String, String>();
			} else {

				List<StemmingBean> beanList = stemmingDAO.loadMapByTopStemmedList(stemmedWordsList, language != null ? language : DEFAULT_LANGUAGE);

				Map<String, WordCountBean> resulProxy = new HashMap<String, WordCountBean>(stemmedWordsList.size() * 2);
				for (StemmingBean bean : beanList) {

					WordCountBean wordCount = resulProxy.get(bean.getStemmedWord());
					if (wordCount != null) {
						if (wordCount.getCount() < bean.getCount()) {
							resulProxy.put(bean.getStemmedWord(), wordCount);
						}
					} else {
						resulProxy.put(bean.getStemmedWord(), new WordCountBean(bean.getWord(), bean.getCount()));
					}

				}
				Map<String, String> result = new HashMap<String, String>(stemmedWordsList.size() * 2);

				for (Map.Entry<String, WordCountBean> proxy : resulProxy.entrySet()) {
					result.put(proxy.getKey(), proxy.getValue().getWord());
				}
				return result;
			}
		} finally {
			serviceMethodPoint.collect();
		}
	}

  /**
   * NEW VERSION
   */

  @Override
  public List<String> getNormalizedTerms(String tweetText, String textLanguage) {
    try {
      // check language
      return PROCESSOR.normalizeString(tweetText, textLanguage);
    } catch (IOException e) {
      e.printStackTrace();
      // make fake result without normalizing
      List<String> r = new ArrayList<String>();
      r.add(tweetText);
      return r;
    }
  }

  /**
   * Can't be run from several threads
   */
  @Override
  public String stem(String term, String textLanguage) {
    if (PROCESSOR.checkStemmingLanguage(textLanguage)) {
      String stemmedResult = PROCESSOR.stemString(term, textLanguage);
      // add to updater process
      sendStemmingResultToProcess(term, stemmedResult, textLanguage);
      return stemmedResult;
    } else {
      return term;
    }
  }

  /**
   * Thread safe method
   */
  @Override
  public String stemWithoutHistory(String term, String textLanguage) {
    if (PROCESSOR.checkStemmingLanguage(textLanguage)) {
      return PROCESSOR.stemString(term, textLanguage);
    } else {
      return term;
    }
  }

}
