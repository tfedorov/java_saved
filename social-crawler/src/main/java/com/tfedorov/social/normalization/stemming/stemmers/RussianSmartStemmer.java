package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.RussianStemmer;

public class RussianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    RussianStemmer stemmer = new RussianStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
