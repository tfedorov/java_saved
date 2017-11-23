package com.tfedorov.social.clustering.jung;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An algorithm for computing clusters (community structure) in graphs based on edge betweenness.
 * The betweenness of an edge is defined as the extent to which that edge lies along shortest paths
 * between all pairs of nodes. This algorithm works by iteratively following the 2 step process: <br>
 * 1) Compute edge betweenness for all edges in current graph <br>
 * 2) Remove edge with highest betweenness <br>
 * Links : <br>
 * http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/cluster/EdgeBetweennessClusterer.
 * html <br>
 * http://www.pnas.org/content/99/12/7821.short<br>
 * http://arxiv.org/pdf/cond-mat/0112110v1.pdf
 * 
 * 
 * @param <V> vertex class
 * @param <E> edge class
 * 
 * @see EdgeWeightTransformer
 * @see BetweennessCentrality
 */
public class EdgeBetweennessClusterer<V, E> implements Transformer<Graph<V, E>, Set<Set<V>>> {
  private int mNumEdgesToRemove;
  private Map<E, Pair<V>> edgesRemoved;

  /**
   * Constructs a new clusterer for the specified graph.
   * 
   * @param numEdgesToRemove the number of edges to be progressively removed from the graph
   */
  public EdgeBetweennessClusterer(int numEdgesToRemove) {
    mNumEdgesToRemove = numEdgesToRemove;
    edgesRemoved = new LinkedHashMap<E, Pair<V>>();
  }

  /**
   * Finds the set of clusters which have the strongest "community structure". The more edges
   * removed the smaller and more cohesive the clusters.
   * 
   * @param graph the graph
   */
  public Set<Set<V>> transform(Graph<V, E> graph) {

    if (mNumEdgesToRemove < 0 || mNumEdgesToRemove > graph.getEdgeCount()) {
      throw new IllegalArgumentException("Invalid number of edges passed in.");
    }

    edgesRemoved.clear();

    for (int k = 0; k < mNumEdgesToRemove; k++) {
      BetweennessCentrality<V, E> bc =
          new BetweennessCentrality<V, E>(graph, new EdgeWeightTransformer());
      E toRemove = null;
      double score = 0;
      for (E e : graph.getEdges()){
        if (bc.getEdgeScore(e) > score) {
          toRemove = e;
          score = bc.getEdgeScore(e);
        }
      }
      edgesRemoved.put(toRemove, graph.getEndpoints(toRemove));
      graph.removeEdge(toRemove);
    }

    WeakComponentClusterer<V, E> wcSearch = new WeakComponentClusterer<V, E>();
    Set<Set<V>> clusterSet = wcSearch.transform(graph);

    for (Map.Entry<E, Pair<V>> entry : edgesRemoved.entrySet()) {
      Pair<V> endpoints = entry.getValue();
      graph.addEdge(entry.getKey(), endpoints.getFirst(), endpoints.getSecond());
    }
    return clusterSet;
  }

  /**
   * Retrieves the list of all edges that were removed (assuming extract(...) was previously
   * called). The edges returned are stored in order in which they were removed.
   * 
   * @return the edges in the original graph
   */
  public List<E> getEdgesRemoved() {
    return new ArrayList<E>(edgesRemoved.keySet());
  }
}
