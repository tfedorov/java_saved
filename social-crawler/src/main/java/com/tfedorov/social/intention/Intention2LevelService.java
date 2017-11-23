package com.tfedorov.social.intention;

import java.util.List;
import java.util.Set;

public interface Intention2LevelService {

  public List<Purchase> getPurchases();

  public boolean isIntention(String text);

  public void reload();

  public void add(List<Purchase> intentLexicons);

  public void save(List<Purchase> intentLexicons);

  public Set<String> getPrimaryTests();

  public List<String> getLexiconLanguage();

}
