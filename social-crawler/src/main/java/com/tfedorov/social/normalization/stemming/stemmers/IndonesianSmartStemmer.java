package com.tfedorov.social.normalization.stemming.stemmers;

import org.apache.lucene.analysis.id.IndonesianStemmer;

public class IndonesianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    IndonesianStemmer stemmer = new IndonesianStemmer();
    int newLength = stemmer.stem(str.toCharArray(), str.length(), false);
    return str.substring(0, newLength);
  }

}
