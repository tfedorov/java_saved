package com.tfedorov.social.clustering.jung;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tfedorov.social.clustering.Cluster;

import edu.uci.ics.jung.algorithms.cluster.VoltageClusterer;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;

/**
 * This class creates clusters based on {@link VoltageClusterer} approach. <br>
 * Links : <br>
 * http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/cluster/VoltageClusterer.html<br>
 * http://www.hpl.hp.com/research/idl/papers/linear/ <br>
 * http://www.hpl.hp.com/research/idl/papers/linear/linear.pdf <br>
 * 
 * 
 */
public class VoltageClustererCalculator extends AbstractJungBasedCalculator {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(VoltageClustererCalculator.class);

  private Graph<String, WeightedEdge> graph;

  @Override
  protected void build() {
    this.graph = createGraph(getWordsPair());
  }

  @Override
  protected boolean checkVertextClusterCount() {
    return getClustersCount() >= graph.getEdgeCount();
  }

  @Override
  protected List<Cluster> calculate() {

    Collection<Set<String>> allClusters = Collections.emptySet();
    
    WeakComponentClusterer<String, WeightedEdge> weakComponentClusterer =
        new WeakComponentClusterer<String, WeightedEdge>();
    Collection<Set<String>> weakClusterSet = weakComponentClusterer.transform(graph);
    
    if (weakClusterSet != null) {
      allClusters = weakClusterSet;
    }

    if (allClusters.size() < getClustersCount()) {
      
      SocialVoltageClusterer<String, WeightedEdge> clusterer =
          new SocialVoltageClusterer<String, WeightedEdge>(graph, getClustersCount());

      Collection<Set<String>> clusterSet = clusterer.cluster(getClustersCount());
      allClusters = clusterSet;
    }
    
    return convertToClusters(allClusters);
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

}
