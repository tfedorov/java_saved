package com.tfedorov.social.clustering;

import java.io.IOException;
import java.util.List;

import com.tfedorov.social.clustering.jung.AbstractJungBasedCalculator;
import com.tfedorov.social.clustering.jung.EdgeBetweennessCalculator;

public class WordsClusterCalculatorOnEdgeBetweennessClient {

  public static void main(String[] args) throws IOException {
    List<CoOccurrenceInfo> list =
        MinimumCutClustererClient
            .loadFromCsvFile("/Users/vhavryk/Documents/workspace-dmp-web/social-algorithms/src/main/resources/result3.csv");
    AbstractJungBasedCalculator calculator =
        new EdgeBetweennessCalculator();
    System.out.println("WordsClusterCalculatorOnEdgeBetweennessClient.main()"
        + calculator.calculate(30, list));
  }
}
