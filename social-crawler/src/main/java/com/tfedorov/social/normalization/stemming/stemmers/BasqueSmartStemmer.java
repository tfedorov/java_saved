package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.BasqueStemmer;

public class BasqueSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    BasqueStemmer stemmer = new BasqueStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
