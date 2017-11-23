package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.ArmenianStemmer;

public class ArmenianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    ArmenianStemmer stemmer = new ArmenianStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
