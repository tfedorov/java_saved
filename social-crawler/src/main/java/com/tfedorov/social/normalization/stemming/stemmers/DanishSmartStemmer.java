package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.DanishStemmer;

public class DanishSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    DanishStemmer stemmer = new DanishStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
