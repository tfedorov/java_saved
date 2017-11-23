package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.RomanianStemmer;

public class RomanianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    RomanianStemmer stemmer = new RomanianStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
