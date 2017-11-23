package com.tfedorov.social.clustering.jgrapht;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.tfedorov.social.clustering.AbstractClusterCalculator;
import com.tfedorov.social.clustering.Cluster;
import com.tfedorov.social.clustering.CoOccurrenceInfo;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Calculates cluster of the words based on {@link StoerWagnerMinimumCut} algorithm from JGraphT
 * library. <br>
 * Links : <br>
 * http://jgrapht.org/javadoc/org/jgrapht/alg/StoerWagnerMinimumCut.html<br>
 * http://dl.acm.org/citation.cfm?id=263872
 * 
 * 
 * 
 */
public class MinimumCutCalculator extends AbstractClusterCalculator {

  private List<SimpleWeightedGraph<String, SocialWeightedEdge>> graphs;

  private SimpleWeightedGraph<String, SocialWeightedEdge> initialGraph;

  private static final Logger LOGGER = LoggerFactory.getLogger(MinimumCutCalculator.class);

  public MinimumCutCalculator() {
    super();
  }

  @Override
  protected void build() {
    initialGraph = createInitialGraph();
    graphs = new ArrayList<SimpleWeightedGraph<String, SocialWeightedEdge>>();
    graphs.add(initialGraph);
  }

  @Override
  protected boolean checkVertextClusterCount() {
    int vertexCount = initialGraph.vertexSet().size();
    return getClustersCount() >= vertexCount;
  }

  protected List<Cluster> calculate() {

    int vertexCount = initialGraph.vertexSet().size();

    getLogger().info("Vertex count : " + vertexCount);

    cutGraphs(this.getClustersCount());
    getLogger().info("Count clusters " + graphs.size());

    return convertGraphsToClusters();
  }

  private List<Cluster> convertGraphsToClusters() {
    List<Cluster> clustersList = new ArrayList<Cluster>();
    for (SimpleWeightedGraph<String, SocialWeightedEdge> graph2 : graphs) {
      Cluster cluster = new Cluster(graph2.vertexSet());
      clustersList.add(cluster);
      getLogger().info(cluster.toString());
    }
    return clustersList;
  }

  /**
   * Cuts graphs from initial graph using {@link SocialStoerWagnerMinimumCut} object.
   * 
   * @param clustersCount count cluster to cut.
   */
  private void cutGraphs(int clustersCount) {
    SocialStoerWagnerMinimumCut<String, SocialWeightedEdge> minimumCut = null;
    do {

      minimumCut = findAppropriateMinimumCut(graphs);
      if (minimumCut == null) {
        break;
      }

      if (graphs.size() >= clustersCount && !hasWeakGraph(minimumCut)) {
        break;
      }
      Set<String> set = minimumCut.minCut();

      SimpleWeightedGraph<String, SocialWeightedEdge> cutGraph;
      cutGraph = cutGraph(minimumCut.getGraph(), set);
      graphs.add(cutGraph);

    }  while (graphs.size() < clustersCount || hasWeakGraph(minimumCut)) ;
  }

  /**
   * This method checks whether specified minimumCut object suggests to cut weak (without any
   * connections to another vertices) graph.
   * 
   * @param minimumCut specified {@link SocialStoerWagnerMinimumCut} object
   * @return true if suggests to cut weak graph
   */
  protected boolean hasWeakGraph(SocialStoerWagnerMinimumCut<String, SocialWeightedEdge> minimumCut) {
    return minimumCut != null && minimumCut.minCutWeight() == 0;
  }

  /**
   * Iterates thought input list of the specified graphs and find appropriate minimum cut
   * {@link SocialStoerWagnerMinimumCut} object.
   * 
   * @param graphs specified list of the graph objects.
   * @return minimum cut {@link SocialStoerWagnerMinimumCut} object
   */
  private SocialStoerWagnerMinimumCut<String, SocialWeightedEdge> findAppropriateMinimumCut(
      final List<SimpleWeightedGraph<String, SocialWeightedEdge>> graphs) {

    SocialStoerWagnerMinimumCut<String, SocialWeightedEdge> minimumCut = null;

    for (SimpleWeightedGraph<String, SocialWeightedEdge> graph : graphs) {

      // calculate minimum cut for current graph
      SocialStoerWagnerMinimumCut<String, SocialWeightedEdge> graphMinimumCut;
      graphMinimumCut = new SocialStoerWagnerMinimumCut<String, SocialWeightedEdge>(graph);

      if (minimumCut == null && graphMinimumCut.minCut() != null
          || (minimumCut != null && graphMinimumCut.minCutWeight() < minimumCut.minCutWeight())) {
        minimumCut = graphMinimumCut;
      }
    }

    return minimumCut;
  }

  /**
   * Cuts specified vertices set from original graph and creates new graph based on vertices set.
   * 
   * @param graph original graph
   * @param set set of vertices
   * @return new graph based on input vertices set.
   */
  private SimpleWeightedGraph<String, SocialWeightedEdge> cutGraph(
      WeightedGraph<String, SocialWeightedEdge> graph, Set<String> set) {

    SimpleWeightedGraph<String, SocialWeightedEdge> cutGraph =
        new SimpleWeightedGraph<String, SocialWeightedEdge>(SocialWeightedEdge.class);

    if (set == null) {
      return cutGraph;
    }

    getLogger().info("Vertex set to cut :" + set);

    // create new graph
    for (String string : set) {
      cutGraph.addVertex(string);
    }

    // add edges based on set of the vertices to new graph
    for (String string : set) {
      Set<SocialWeightedEdge> edges = graph.edgesOf(string);
      for (SocialWeightedEdge edge : edges) {
        if (set.contains(edge.getSource()) && set.contains(edge.getTarget())) {

          addEdge(cutGraph, edge.getSource().toString(), edge.getTarget().toString(),
              edge.getWeight());
        }
      }
    }

    // remove (cut) vertices from original graph
    graph.removeAllVertices(set);
    return cutGraph;
  }

  private SimpleWeightedGraph<String, SocialWeightedEdge> createInitialGraph() {

    final SimpleWeightedGraph<String, SocialWeightedEdge> graph =
        new SimpleWeightedGraph<String, SocialWeightedEdge>(SocialWeightedEdge.class);
    populateGraph(graph);
    return graph;
  }

  /**
   * Populate graph by list of the {@link CoOccurrenceInfo} objects.
   * 
   * @param graph grapch to populate
   */
  private void populateGraph(SimpleWeightedGraph<String, SocialWeightedEdge> graph) {
    for (CoOccurrenceInfo pair : getWordsPair()) {
      addVertex(graph, pair.getFirstWord());
      addVertex(graph, pair.getSecondWord());
      if (!pair.getFirstWord().equals(pair.getSecondWord())) {
        addEdge(graph, pair.getFirstWord(), pair.getSecondWord(), pair.getCountLinks());
      }
    }
  }


  private void addEdge(SimpleWeightedGraph<String, SocialWeightedEdge> graph, String firstWord,
      String secondWord, double countLinks) {

    SocialWeightedEdge edge = graph.addEdge(firstWord, secondWord);
    if (edge != null) {
      graph.setEdgeWeight(edge, countLinks);
    }
  }

  private void addVertex(SimpleWeightedGraph<String, SocialWeightedEdge> graph, String word) {
    if (!graph.containsVertex(word)) {
      graph.addVertex(word);
    }
  }


  SimpleWeightedGraph<String, SocialWeightedEdge> getInitialGraph() {
    return initialGraph;
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }



}
