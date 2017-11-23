package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.IrishStemmer;

public class IrishSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    IrishStemmer stemmer = new IrishStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
