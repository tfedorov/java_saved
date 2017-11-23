package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.SwedishStemmer;

public class SwedishSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    SwedishStemmer stemmer = new SwedishStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
