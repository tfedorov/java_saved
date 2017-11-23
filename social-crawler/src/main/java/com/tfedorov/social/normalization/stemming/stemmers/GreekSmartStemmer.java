package com.tfedorov.social.normalization.stemming.stemmers;

import org.apache.lucene.analysis.el.GreekStemmer;

public class GreekSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    GreekStemmer stemmer = new GreekStemmer();
    int newLength = stemmer.stem(str.toCharArray(), str.length());
    return str.substring(0, newLength);
  }

}
