package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.PortugueseStemmer;

public class PortugueseSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    PortugueseStemmer stemmer = new PortugueseStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }
}
