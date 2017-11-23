package com.tfedorov.social.normalization.stemming.stemmers;

import org.apache.lucene.analysis.gl.GalicianStemmer;

public class GalicianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    GalicianStemmer stemmer = new GalicianStemmer();
    int newLength = stemmer.stem(str.toCharArray(), str.length());
    return str.substring(0, newLength);
  }

}
