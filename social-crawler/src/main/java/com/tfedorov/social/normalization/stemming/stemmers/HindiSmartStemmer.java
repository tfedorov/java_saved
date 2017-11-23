package com.tfedorov.social.normalization.stemming.stemmers;

import org.apache.lucene.analysis.hi.HindiStemmer;

public class HindiSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    HindiStemmer stemmer = new HindiStemmer();
    int newLength = stemmer.stem(str.toCharArray(), str.length());
    return str.substring(0, newLength);
  }
}
