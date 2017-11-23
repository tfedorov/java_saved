package com.tfedorov.social.normalization.stemming;

import org.joda.time.DateTime;

public class StemmingResult {
  private String word;
  private String stemmedWord;
  private String language;

  private DateTime modificationDate;

  public StemmingResult(String word, String stemmedWord, String language) {
    this.word = word;
    this.stemmedWord = stemmedWord;
    this.language = language;
  }


  public StemmingResult(String word, String stemmedWord, String language, DateTime modificationDate) {
    super();
    this.word = word;
    this.stemmedWord = stemmedWord;
    this.language = language;
    this.modificationDate = modificationDate;
  }

  public String getWord() {
    return word;
  }

  public String getStemmedWord() {
    return stemmedWord;
  }

  public String getLanguage() {
    return language;
  }

  public DateTime getModificationDate() {
    return modificationDate;
  }


  public void setModificationDate(DateTime modificationDate) {
    this.modificationDate = modificationDate;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((language == null) ? 0 : language.hashCode());
    result = prime * result + ((stemmedWord == null) ? 0 : stemmedWord.hashCode());
    result = prime * result + ((word == null) ? 0 : word.hashCode());
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StemmingResult other = (StemmingResult) obj;
    if (language == null) {
      if (other.language != null) {
        return false;
      }
    } else if (!language.equals(other.language)) {
      return false;
    }
    if (stemmedWord == null) {
      if (other.stemmedWord != null) {
        return false;
      }
    } else if (!stemmedWord.equals(other.stemmedWord)) {
      return false;
    }
    if (word == null) {
      if (other.word != null) {
        return false;
      }
    } else if (!word.equals(other.word)) {
      return false;
    }
    return true;
  }


}
