package com.tfedorov.social.intention.dao;

import java.util.List;

import com.tfedorov.social.intention.IntentLexicon;

public interface IntentLexiconDao {

  public List<IntentLexicon> getIntentLexicons();

  public int[] insertIntentLexicons(List<IntentLexicon> intentLexicons);

  public List<String> getLexiconLanguages();

}
