package com.tfedorov.social.clustering.jgrapht;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.StoerWagnerMinimumCut;

public class SocialStoerWagnerMinimumCut<V, E> extends StoerWagnerMinimumCut<V, E> {

  private WeightedGraph<V, E> graph;

  public SocialStoerWagnerMinimumCut(WeightedGraph<V, E> graph) {
    super(graph);
    this.graph = graph;
  }

  public WeightedGraph<V, E> getGraph() {
    return graph;
  }



}
