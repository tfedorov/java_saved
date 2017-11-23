package com.tfedorov.social.normalization.stemming.stemmers;

import org.tartarus.snowball.ext.ItalianStemmer;

public class ItalianSmartStemmer implements SmartStemmer {

  @Override
  public String stem(String str) {
    ItalianStemmer stemmer = new ItalianStemmer();
    stemmer.setCurrent(str);
    stemmer.stem();
    return stemmer.getCurrent();
  }

}
