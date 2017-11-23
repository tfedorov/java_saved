package com.tfedorov.social.clustering;

import java.util.Collections;
import java.util.Set;

/**
 * This class is container of the list of the words.
 * 
 * @author vitalij havryk
 * 
 */
public class Cluster {

  private Set<String> words;

  public Cluster(Set<String> words) {
    this.words = words;
  }

  public Set<String> getWords() {
    return Collections.unmodifiableSet(words);
  }

  @Override
  public String toString() {
    return "Cluster [words=" + words + "] \r\n";
  }
  

}
