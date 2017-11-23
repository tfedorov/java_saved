package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.GermanStemmer;

public class GermanSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    GermanStemmer stemmer = new GermanStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
