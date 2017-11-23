package com.tfedorov.social.intention.dao;

import java.util.List;

import com.tfedorov.social.intention.Purchase;

public interface Intention2LevelDao {

  public List<Purchase> getLevel2Lexicons();

  public int[] insertLevel2Lexicons(List<Purchase> intentLexicons);

  public List<String> getLexiconLanguages();

}
