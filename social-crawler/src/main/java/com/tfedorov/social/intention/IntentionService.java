package com.tfedorov.social.intention;

import java.util.Set;

public interface IntentionService {

  public boolean isIntention(String text);

  public IntentString isIntentionString(String normalizedText, String originalText);

  public Set<String> getAllIntentTrms();

  public void reloadIntentions();

  boolean isLanguageExist(String lang);
}
