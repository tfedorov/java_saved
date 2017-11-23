package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.NorwegianStemmer;

public class NorwegianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    NorwegianStemmer stemmer = new NorwegianStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
