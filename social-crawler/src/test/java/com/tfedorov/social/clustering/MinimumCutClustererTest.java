package com.tfedorov.social.clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tfedorov.social.clustering.jgrapht.MinimumCutCalculator;
import junit.framework.Assert;

import org.junit.Test;

public class MinimumCutClustererTest extends AbstractWordClusterCalculatorTest {

	@Override
	protected ClustersCalculator getWordsClusterCalculator() {
		return new MinimumCutCalculator();
	}

	protected List<CoOccurrenceInfo> createAleadyClusteredTestPairs() {
		List<CoOccurrenceInfo> wordsPair = new ArrayList<CoOccurrenceInfo>();
		addConnection(wordsPair, opensource, aws, 5);
		addConnection(wordsPair, Developer, Hadoop, 6);
		addConnection(wordsPair, datamart, JAVA, 10);
		addConnection(wordsPair, MapReduce, HFS, 30);
		return wordsPair;
	}

	@Test
	public void testAleadyClusteredWords() {
		List<CoOccurrenceInfo> wordsPair = createAleadyClusteredTestPairs();
		ClustersCalculator calculatorOnMinimumCut = getWordsClusterCalculator();
		List<Cluster> clusters = calculatorOnMinimumCut.calculate(1, wordsPair);
		Assert.assertEquals(4, clusters.size());
	}

	@Test
	public void testIssueAXS3() throws IOException {
		List<CoOccurrenceInfo> wordsPairs = MinimumCutClustererClient
				.loadFromCsvFile("src/test/resources/AXS-3.csv");
		ClustersCalculator calculatorOnMinimumCut = getWordsClusterCalculator();
		List<Cluster> clusters = calculatorOnMinimumCut
				.calculate(2, wordsPairs);
		Assert.assertEquals(3, clusters.size());
	}

}
