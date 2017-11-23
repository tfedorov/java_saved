package com.tfedorov.social.normalization.stemming;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StemmingService {
  String DEFAULT_LANGUAGE = "en";

  @Deprecated
  String steamStringLine(String text, String language);

  @Deprecated
  List<String> steamStringLineToList(String text, String language);

  void disableUpdateHistory();

  void enableUpdateHistory();

  boolean isStemmingHistoryEnabled();

  Integer getQueueSize();

  Map<String, String> loadTopWordsListByStemmed(final List<String> stemmedWordsList,
      final String language);

  void cleanUselessSteam();

  List<String> getNormalizedTerms(String tweetText, String textLanguage);

  String stem(String term, String textLanguage);

  void initializeAnalyzers(Set<String> stopWords);

  String stemWithoutHistory(String term, String textLanguage);
}
