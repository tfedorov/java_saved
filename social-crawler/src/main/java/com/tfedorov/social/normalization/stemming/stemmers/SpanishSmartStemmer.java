package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.SpanishStemmer;

public class SpanishSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    SpanishStemmer stemmer = new SpanishStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
