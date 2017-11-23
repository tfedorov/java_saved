package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.FrenchStemmer;

public class FrenchSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    FrenchStemmer stemmer = new FrenchStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
