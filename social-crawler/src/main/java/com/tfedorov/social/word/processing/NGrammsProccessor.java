package com.tfedorov.social.word.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NGrammsProccessor {

  private NGrammsProccessor() {}

  public static List<String> splitToNGramms(List<String> splittedText, int grammsDimension) {
    if (grammsDimension < 1) {
      throw new IllegalArgumentException(
          "Invalid argument. grammsDimension should be greater or equal 1");
    }

    List<String> nGramms = new ArrayList<String>(splittedText.size());
    int splittedTextSize = splittedText.size();
    for (int lemma = 0; lemma < splittedTextSize; lemma++) {
      if (lemma + grammsDimension <= splittedTextSize) {
        StringBuilder sb = new StringBuilder();
        for (int element = lemma; element < (lemma + grammsDimension); element++) {
          sb.append(splittedText.get(element)).append(" ");
        }
        nGramms.add(sb.toString().trim());
      }
    }
    return nGramms;
  }

  public static List<String> splitTo2Gramms(List<String> splittedText) {
    int size = splittedText.size();
    if (size <= 1) {
      return Collections.emptyList();
    }

    List<String> nGramms = new ArrayList<String>(size);
    for (int i = 0; i < size - 1; i++) {
      for (int j = i + 1; j < size; j++) {
        nGramms.add(splittedText.get(i) + " " + splittedText.get(j));
      }
    }
    return nGramms;
  }

}
