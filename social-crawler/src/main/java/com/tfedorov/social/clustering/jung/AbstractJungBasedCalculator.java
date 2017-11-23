package com.tfedorov.social.clustering.jung;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.tfedorov.social.clustering.AbstractClusterCalculator;
import com.tfedorov.social.clustering.Cluster;
import com.tfedorov.social.clustering.CoOccurrenceInfo;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public abstract class AbstractJungBasedCalculator extends AbstractClusterCalculator {

  public AbstractJungBasedCalculator() {
    super();
  }

  protected List<Cluster> convertToClusters(Collection<Set<String>> clusterSet) {
    List<Cluster> clusters = new ArrayList<Cluster>();
    for (Set<String> set : clusterSet) {
      Cluster cluster = new Cluster(set);
      clusters.add(cluster);
    }
    return clusters;
  }

  protected Graph<String, WeightedEdge> createGraph(List<CoOccurrenceInfo> wordsPairs) {
  
    Graph<String, WeightedEdge> graph =
        new UndirectedSparseGraph<String, WeightedEdge>();
  
    List<String> words = new ArrayList<String>();
    for (CoOccurrenceInfo wordsPair : wordsPairs) {
      addWord(words, wordsPair.getFirstWord());
      addWord(words, wordsPair.getSecondWord());
    }
  
    // add vertex to graph
    for (String string : words) {
      graph.addVertex(string);
    }
  
    // add edges to graph
    for (CoOccurrenceInfo wordsPair : wordsPairs) {
      String source = wordsPair.getFirstWord();
      String target = wordsPair.getSecondWord();
      WeightedEdge myLink = new WeightedEdge(wordsPair.getCountLinks(), source, target);
      graph.addEdge(myLink, source, target, EdgeType.UNDIRECTED);
    }
  
    return graph;
  }

  private void addWord(List<String> words, String word) {
    if (!words.contains(word)) {
      words.add(word);
    }
  }

}
