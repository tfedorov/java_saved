package com.tfedorov.social.stemming.dao;

import java.util.List;
import java.util.Map;

import com.tfedorov.social.normalization.stemming.StemmingResult;
import org.joda.time.DateTime;

public interface StemmingDAO {

  String loadTopWordByStemmed(String stemmedWord);

  Map<String, String> loadTopWordsListByStemmed(List<String> stemmedWordsList, final String language);

  int[] addNewStemmedWord(Map<StemmingResult, Integer> insertList);

  int[] updateStemmedWord(StemmingResult[] stemmingResult);

  int cleanUselessSteamLimited(DateTime from, DateTime to, String lang);

  List<StemmingResult> getStemmingByWords(String originalForm);
  
  List<String> getStemmingLangsByCount();

  List<StemmingBean> loadMapByTopStemmedList(List<String> stemmedWordsList, String language);

}
