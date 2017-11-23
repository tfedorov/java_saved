package com.tfedorov.social.clustering.jung;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tfedorov.social.clustering.Cluster;
import com.tfedorov.social.clustering.CoOccurrenceInfo;

import edu.uci.ics.jung.graph.Graph;

/**
 * This class calculates clusters for list of the {@link CoOccurrenceInfo} objects by specified
 * clustersCount input parameter. This algorithm based on {@link EdgeBetweennessClusterer}
 * class.
 * 
 * 
 */
public class EdgeBetweennessCalculator extends AbstractJungBasedCalculator {

  private static final int DEFAULT_ITERATION_COUNT = 10;
  private static final Logger LOGGER = LoggerFactory
      .getLogger(EdgeBetweennessCalculator.class);
  private Graph<String, WeightedEdge> graph;

  @Override
  protected void build() {
    this.graph = createGraph(getWordsPair());
  }

  @Override
  protected boolean checkVertextClusterCount() {
    return getClustersCount() >= graph.getEdgeCount();
  }

  protected List<Cluster> calculate() {

    // cluster graph to weak (no connections to another vertices) subgraphs
    int numEdgesToRemove = 0;
    int iterations = DEFAULT_ITERATION_COUNT;
    Set<Set<String>> clusterSet = Collections.emptySet();

    for (int i = 0; i < iterations; i++) {

      EdgeBetweennessClusterer<String, WeightedEdge> clusterer =
          new EdgeBetweennessClusterer<String, WeightedEdge>(numEdgesToRemove);

      clusterSet = clusterer.transform(graph);

      if (clusterSet.size() >= getClustersCount()) {
        break;
      }

      // increase number of the edges to removing at next iteration.
      numEdgesToRemove = numEdgesToRemove + getClustersCount() - clusterSet.size();
    }

    return convertToClusters(clusterSet);
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

}
