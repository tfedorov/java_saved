package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.EnglishStemmer;

public class EnglishSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    EnglishStemmer stemmer = new EnglishStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
