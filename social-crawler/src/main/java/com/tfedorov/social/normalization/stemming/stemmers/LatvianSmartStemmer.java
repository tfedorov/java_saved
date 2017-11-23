package com.tfedorov.social.normalization.stemming.stemmers;

import org.apache.lucene.analysis.lv.LatvianStemmer;

public class LatvianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    LatvianStemmer stemmer = new LatvianStemmer();
    int newLength = stemmer.stem(str.toCharArray(), str.length());
    return str.substring(0, newLength);

  }
}
