package com.tfedorov.social.intention;

import java.util.List;
import java.util.Set;

public interface Intention1LevelService {

  @Deprecated
  public List<IntentLexicon> getIntentLexicons();

  public Set<String> getIntentSearchTerms();

  public boolean isIntention(String text);

  public IntentString isIntentionString(String normalizedText, String originalText);

  public void reload();

  @Deprecated
  public void add(List<IntentLexicon> intentLexicons);

  @Deprecated
  public void save(List<IntentLexicon> intentLexicons);

  public List<String> getLexiconLanguage();

}
