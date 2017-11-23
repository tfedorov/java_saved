package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.FinnishStemmer;

public class FinnishSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    FinnishStemmer stemmer = new FinnishStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
