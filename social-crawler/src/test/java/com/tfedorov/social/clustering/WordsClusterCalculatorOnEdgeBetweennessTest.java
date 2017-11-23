package com.tfedorov.social.clustering;

import com.tfedorov.social.clustering.jung.EdgeBetweennessCalculator;

public class WordsClusterCalculatorOnEdgeBetweennessTest extends AbstractWordClusterCalculatorTest {

  @Override
  protected ClustersCalculator getWordsClusterCalculator() {
    return new EdgeBetweennessCalculator();
  }

}
