package com.tfedorov.social.normalization.stemming.stemmers;

import org.apache.lucene.analysis.ar.ArabicStemmer;

public class ArabicSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String string) {
    ArabicStemmer stemmer = new ArabicStemmer();
    int newLength = stemmer.stem(string.toCharArray(), string.length());
    return string.substring(0, newLength);
  }

}
