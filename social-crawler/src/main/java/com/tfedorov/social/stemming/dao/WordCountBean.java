/**
 * 
 */
package com.tfedorov.social.stemming.dao;

/**
 * @author tfedorov
 * 
 */
public class WordCountBean {

  private String word;
  private Integer count;

  public WordCountBean(String word, Integer count) {
    super();
    this.word = word;
    this.count = count;
  }

  public String getWord() {
    return word;
  }

  public Integer getCount() {
    return count;
  }

}
