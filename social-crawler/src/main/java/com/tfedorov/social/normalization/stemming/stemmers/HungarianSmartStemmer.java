package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.HungarianStemmer;

public class HungarianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    HungarianStemmer stemmer = new HungarianStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
