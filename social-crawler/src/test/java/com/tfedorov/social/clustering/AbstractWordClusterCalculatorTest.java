package com.tfedorov.social.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;



public abstract class AbstractWordClusterCalculatorTest {

  protected static final String JAVA = "Java";
  protected static final String datamart = "datamart";
  protected static final String aws = "aws";
  protected static final String opensource = "opensource";
  protected static final String Developer = "Developer";
  protected static final String Hadoop = "Hadoop";
  protected static final String Hbase = "Hbase";
  protected static final String HFS = "HFS";
  protected static final String MapReduce = "MapReduce";
  protected static final String Algoritnms = "Algoritnms";
  protected static final String KMeans = "KMeans";
  protected static final String MinCut = "MinCut";
  protected static final String Alone = "Alone";

  private EtmMonitor etmMonitor;

  public AbstractWordClusterCalculatorTest() {
    super();
  }

  @Before
  public void beforeTests() {
    BasicEtmConfigurator.configure();
    etmMonitor = EtmManager.getEtmMonitor();
    etmMonitor.start();
  }

  @After
  public void afterTests() {
    etmMonitor.stop();
    // etmMonitor.render(new SimpleTextRenderer());
  }

  @Test
  public void testCalculateClustersSize() {
    List<CoOccurrenceInfo> wordsPair = createTestPairs();
    ClustersCalculator calculatorOnMinimumCut = getWordsClusterCalculator();
    List<Cluster> clusters = calculatorOnMinimumCut.calculate(4, wordsPair);
    Assert.assertEquals(4, clusters.size());
  }

  @Test
  public void testVertexClusterCount() {
    List<CoOccurrenceInfo> wordsPair = createTestPairs();
    final Set<String> clusterPair = new HashSet<String>();
    for (final CoOccurrenceInfo pair : wordsPair) {
      clusterPair.add(pair.getFirstWord());
      clusterPair.add(pair.getSecondWord());
    }
    ClustersCalculator calculatorOnMinimumCut = getWordsClusterCalculator();
    List<Cluster> clusters = calculatorOnMinimumCut.calculate(Integer.MAX_VALUE, wordsPair);
    Assert.assertEquals(clusterPair.size(), clusters.size());
  }

  @Test
  public void testCalculateClustersContent() {
    List<CoOccurrenceInfo> wordsPair = createTestPairs();
    ClustersCalculator calculatorOnMinimumCut = getWordsClusterCalculator();
    List<Cluster> clusters = calculatorOnMinimumCut.calculate(4, wordsPair);
    assertContains(clusters, opensource, aws, datamart, Developer, Hadoop, JAVA, Hbase, HFS,
        MapReduce);
    assertContains(clusters, Alone);
    assertContains(clusters, MinCut, Algoritnms, KMeans);
    assertNotContains(clusters, Alone, Algoritnms);
    assertNotContains(clusters, Alone, datamart);
    assertNotContains(clusters, datamart, Algoritnms);
  }


  protected void assertContains(List<Cluster> clusters, String... strings) {
    List<String> stringList = Arrays.asList(strings);
    if (!assertHas(clusters, stringList)) {
      Assert.fail("Clusters :" + clusters + " didn't contain words set" + stringList);
    }
  }

  protected void assertNotContains(List<Cluster> clusters, String... strings) {
    List<String> stringList = Arrays.asList(strings);
    if (assertHas(clusters, stringList)) {
      Assert.fail("Clusters :" + clusters + " contain words set" + stringList);
    }
  }

  private boolean assertHas(List<Cluster> clusters, List<String> strings) {
    boolean has = false;
    for (Cluster cluster : clusters) {
      if (cluster.getWords().containsAll(strings)) {
        has = true;
        break;
      }
    }
    return has;
  }

  @Test
  public void testCalculateClustersNotNull() {
    List<CoOccurrenceInfo> wordsPair = createTestPairs();
    ClustersCalculator calculatorOnMinimumCut = getWordsClusterCalculator();
    List<Cluster> clusters = calculatorOnMinimumCut.calculate(4, wordsPair);
    Assert.assertNotNull(clusters);
  }

  protected abstract ClustersCalculator getWordsClusterCalculator();

  protected List<CoOccurrenceInfo> createTestPairs() {
    List<CoOccurrenceInfo> wordsPair = new ArrayList<CoOccurrenceInfo>();
    addConnection(wordsPair, opensource, aws, 5);
    addConnection(wordsPair, aws, datamart, 3);
    addConnection(wordsPair, Developer, Hadoop, 6);
    addConnection(wordsPair, aws, Developer, 2);
    addConnection(wordsPair, aws, Hadoop, 9);
    addConnection(wordsPair, Developer, opensource, 7);
    addConnection(wordsPair, opensource, datamart, 10);
    addConnection(wordsPair, datamart, Hadoop, 1);
    addConnection(wordsPair, datamart, JAVA, 10);
    addConnection(wordsPair, opensource, JAVA, 11);
    addConnection(wordsPair, datamart, Developer, 6);
    addConnection(wordsPair, Hadoop, Hbase, 26);
    addConnection(wordsPair, Hadoop, HFS, 30);
    addConnection(wordsPair, MapReduce, HFS, 30);
    addConnection(wordsPair, datamart, Algoritnms, 10);
    addConnection(wordsPair, KMeans, Algoritnms, 30);
    addConnection(wordsPair, MinCut, Algoritnms, 30);
    addConnection(wordsPair, Alone, Alone, 30);
    addConnection(wordsPair, "test", "test", 30);
    return wordsPair;
  }

  protected void addConnection(List<CoOccurrenceInfo> wordsPairs, String word1, String word2,
      int cooccurence) {
    CoOccurrenceInfo wordsPair = new CoOccurrenceInfo(word1, word2, cooccurence);
    wordsPairs.add(wordsPair);
  }

}
