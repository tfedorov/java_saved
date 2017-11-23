/**
 * 
 */
package com.tfedorov.social.stemming.dao;

/**
 * @author tfedorov
 * 
 */
public class StemmingBean {

  private String stemmedWord;
  private WordCountBean wordCountBean;

  public StemmingBean(String stemmedWord, String word, Integer count) {
    super();
    this.stemmedWord = stemmedWord;
    this.wordCountBean = new WordCountBean(word, count);
  }

  public String getStemmedWord() {
    return stemmedWord;
  }

  public String getWord() {
    return wordCountBean.getWord();
  }

  public Integer getCount() {
    return wordCountBean.getCount();
  }
}
