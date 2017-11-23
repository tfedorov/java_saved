package com.tfedorov.social.word.processing;

import java.util.List;

public class NormalizedTweetText {
  private String stemmedNormalizedText;
  private String normalizedText;
  private String cleanText;
  private String preparedText;
  private List<String> textTerms;
  private List<String> stemmedTextTerms;


  public List<String> getTextTerms() {
    return textTerms;
  }

  public void setTextTerms(List<String> textTerms) {
    this.textTerms = textTerms;
  }

  public List<String> getStemmedTextTerms() {
    return stemmedTextTerms;
  }

  public void setStemmedTextTerms(List<String> stemmedTextTerms) {
    this.stemmedTextTerms = stemmedTextTerms;
  }

  public String getStemmedNormalizedText() {
    return stemmedNormalizedText;
  }

  public void setStemmedNormalizedText(String stemmedNormalizedText) {
    this.stemmedNormalizedText = stemmedNormalizedText;
  }

  public String getNormalizedText() {
    return normalizedText;
  }

  public void setNormalizedText(String normalizedText) {
    this.normalizedText = normalizedText;
  }

  public String getCleanText() {
    return cleanText;
  }

  public void setCleanText(String clearText) {
    this.cleanText = clearText;
  }

  public String getPreparedText() {
    return preparedText;
  }

  public void setPreparedText(String preparedText) {
    this.preparedText = preparedText;
  }

}
