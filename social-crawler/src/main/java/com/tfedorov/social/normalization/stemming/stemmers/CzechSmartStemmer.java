package com.tfedorov.social.normalization.stemming.stemmers;

import org.apache.lucene.analysis.cz.CzechStemmer;

public class CzechSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    CzechStemmer stemmer = new CzechStemmer();
    int newLength = stemmer.stem(str.toCharArray(), str.length());
    return str.substring(0, newLength);
  }

}
