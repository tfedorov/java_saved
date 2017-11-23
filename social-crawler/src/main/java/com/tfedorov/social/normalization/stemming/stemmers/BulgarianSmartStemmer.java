package com.tfedorov.social.normalization.stemming.stemmers;

import org.apache.lucene.analysis.bg.BulgarianStemmer;

public class BulgarianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    BulgarianStemmer stemmer = new BulgarianStemmer();
    int newLength = stemmer.stem(str.toCharArray(), str.length());
    return str.substring(0, newLength);
  }

}
