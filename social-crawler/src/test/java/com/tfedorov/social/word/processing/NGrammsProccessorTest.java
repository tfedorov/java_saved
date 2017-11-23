package com.tfedorov.social.word.processing;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class NGrammsProccessorTest {
  @Test
  public void testNGrammsProcessor() {
    List<String> splittedText = new ArrayList<String>();

    splittedText.add("word1");
    splittedText.add("word2");
    splittedText.add("word3");
    splittedText.add("word4");

    try {
      NGrammsProccessor.splitToNGramms(splittedText, 0);
      assertTrue(false);
    } catch (Exception e) {
      assertTrue("This case should throw exception. " + e.getMessage(), true);

    }

    List<String> processedUnoGramms = NGrammsProccessor.splitToNGramms(splittedText, 1);
    assertEquals("word1", processedUnoGramms.get(0));
    assertEquals("word2", processedUnoGramms.get(1));
    assertEquals("word3", processedUnoGramms.get(2));
    assertEquals("word4", processedUnoGramms.get(3));

    List<String> processedBiGramms = NGrammsProccessor.splitToNGramms(splittedText, 2);
    assertEquals("word1 word2", processedBiGramms.get(0));
    assertEquals("word2 word3", processedBiGramms.get(1));
    assertEquals("word3 word4", processedBiGramms.get(2));

    List<String> processedTriGramms = NGrammsProccessor.splitToNGramms(splittedText, 3);
    assertEquals("word1 word2 word3", processedTriGramms.get(0));
    assertEquals("word2 word3 word4", processedTriGramms.get(1));
  }

  @Test
  public void testTwoTermsGrammsProcessor() {
    assertTrue(NGrammsProccessor.splitTo2Gramms(Collections.EMPTY_LIST).isEmpty());

    assertTrue(NGrammsProccessor.splitTo2Gramms(Collections.singletonList("testWord"))
        .isEmpty());


    List<String> splittedText = new ArrayList<String>();

    splittedText.add("word1");
    splittedText.add("word2");
    splittedText.add("word3");
    splittedText.add("word4");

    List<String> list = NGrammsProccessor.splitTo2Gramms(splittedText);
    assertEquals(6, list.size());

    assertEquals("word1 word2", list.get(0));
    assertEquals("word1 word3", list.get(1));
    assertEquals("word1 word4", list.get(2));
    assertEquals("word2 word3", list.get(3));
    assertEquals("word2 word4", list.get(4));
    assertEquals("word3 word4", list.get(5));
  }

  @Test
  public void testTwoTermsGrammsForUnsortedTermsListProcessor() {

    //back ordered list
    List<String> splittedText = new ArrayList<String>();

    splittedText.add("CCC");
    splittedText.add("BBB");
    splittedText.add("AAA");

    List<String> list = NGrammsProccessor.splitTo2Gramms(splittedText);
    assertEquals(3, list.size());

    assertEquals("CCC BBB", list.get(0));
    assertEquals("CCC AAA", list.get(1));
    assertEquals("BBB AAA", list.get(2));

    //unordered list
    splittedText = new ArrayList<String>();

    splittedText.add("CCC");
    splittedText.add("AAA");
    splittedText.add("BBB");

    list = NGrammsProccessor.splitTo2Gramms(splittedText);
    assertEquals(3, list.size());

    assertEquals("CCC AAA", list.get(0));
    assertEquals("CCC BBB", list.get(1));
    assertEquals("AAA BBB", list.get(2));

  
  }

}
