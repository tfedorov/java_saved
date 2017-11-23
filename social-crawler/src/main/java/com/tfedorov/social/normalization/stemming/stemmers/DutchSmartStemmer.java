package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.DutchStemmer;

public class DutchSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    DutchStemmer stemmer = new DutchStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
