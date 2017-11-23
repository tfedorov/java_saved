package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.CatalanStemmer;

public class CatalanSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    CatalanStemmer stemmer = new CatalanStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
