package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.TurkishStemmer;

public class TurkishSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    TurkishStemmer stemmer = new TurkishStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
