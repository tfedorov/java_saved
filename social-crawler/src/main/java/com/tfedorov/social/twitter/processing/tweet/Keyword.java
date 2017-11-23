package com.tfedorov.social.twitter.processing.tweet;

public class Keyword {
  private String normalKeyword;
  private String stemmedKeyword;

  public String getNormalKeyword() {
    return normalKeyword;
  }

  public void setNormalKeyword(String normalKeyword) {
    this.normalKeyword = normalKeyword;
  }

  public String getStemmedKeyword() {
    return stemmedKeyword;
  }

  public void setStemmedKeyword(String stemmedKeyword) {
    this.stemmedKeyword = stemmedKeyword;
  }

}
