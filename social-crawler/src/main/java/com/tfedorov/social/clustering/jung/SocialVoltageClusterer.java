package com.tfedorov.social.clustering.jung;

/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University of California All rights
 * reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Aug 12, 2004
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.algorithms.scoring.VoltageScorer;
import edu.uci.ics.jung.algorithms.util.DiscreteDistribution;
import edu.uci.ics.jung.algorithms.util.KMeansClusterer;
import edu.uci.ics.jung.algorithms.util.KMeansClusterer.NotEnoughClustersException;
import edu.uci.ics.jung.graph.Graph;

/**
 * <p>
 * Clusters vertices of a <code>Graph</code> based on their ranks as calculated by
 * <code>VoltageScorer</code>. This algorithm is based on, but not identical with, the method
 * described in the paper below. The primary difference is that Wu and Huberman assume a priori that
 * the clusters are of approximately the same size, and therefore use a more complex method than
 * k-means (which is used here) for determining cluster membership based on co-occurrence data.
 * </p>
 * 
 * <p>
 * The algorithm proceeds as follows:
 * <ul>
 * <li/>first, generate a set of candidate clusters as follows:
 * <ul>
 * <li/>pick (widely separated) vertex pair, run VoltageScorer
 * <li/>group the vertices in two clusters according to their voltages
 * <li/>store resulting candidate clusters
 * </ul>
 * <li/>second, generate k-1 clusters as follows:
 * <ul>
 * <li/>pick a vertex v as a cluster 'seed' <br>
 * (Wu/Huberman: most frequent vertex in candidate clusters)
 * <li/>calculate co-occurrence over all candidate clusters of v with each other vertex
 * <li/>separate co-occurrence counts into high/low; high vertices constitute a cluster
 * <li/>remove v's vertices from candidate clusters; continue
 * </ul>
 * <li/>finally, remaining unassigned vertices are assigned to the kth ("garbage") cluster.
 * </ul>
 * </p>
 * 
 * <p>
 * <b>NOTE</b>: Depending on how the co-occurrence data splits the data into clusters, the number of
 * clusters returned by this algorithm may be less than the number of clusters requested. The number
 * of clusters will never be more than the number requested, however.
 * </p>
 * 
 * @author Joshua O'Madadhain
 * @see "'Finding communities in linear time: a physics approach', Fang Wu and Bernardo Huberman, http://www.hpl.hp.com/research/idl/papers/linear/"
 * @see VoltageScorer
 * @see KMeansClusterer
 */
public class SocialVoltageClusterer<V, E> {
  private int numCandidates;
  private KMeansClusterer<V> kmc;
  private Random rand;
  private Graph<V, E> g;

  /**
   * Creates an instance of a VoltageCluster with the specified parameters. These are mostly
   * parameters that are passed directly to VoltageScorer and KMeansClusterer.
   * 
   * @param numCandidates the number of candidate clusters to create
   */
  public SocialVoltageClusterer(Graph<V, E> g, int numCandidates) {
    if (numCandidates < 1) {
      throw new IllegalArgumentException("must generate >=1 candidates");
    }

    this.numCandidates = numCandidates;
    this.kmc = new KMeansClusterer<V>();
    rand = new Random();
    this.g = g;
  }

  protected void setRandomSeed(int randomSeed) {
    rand = new Random(randomSeed);
  }

  /**
   * Returns a community (cluster) centered around <code>v</code>.
   * 
   * @param v the vertex whose community we wish to discover
   */
  public Collection<Set<V>> getCommunity(V v) {
    return cluster_internal(v, 2);
  }

  /**
   * Clusters the vertices of <code>g</code> into <code>numClusters</code> clusters, based on their
   * connectivity.
   * 
   * @param numClusters the number of clusters to identify
   */
  public Collection<Set<V>> cluster(int numClusters) {
    return cluster_internal(null, numClusters);
  }

  /**
   * Does the work of <code>getCommunity</code> and <code>cluster</code>.
   * 
   * @param origin the vertex around which clustering is to be done
   * @param numClusters the (maximum) number of clusters to find
   */
  protected Collection<Set<V>> cluster_internal(V origin, int numClusters) {
    // generate candidate clusters
    // repeat the following 'samples' times:
    // * pick (widely separated) vertex pair, run VoltageScorer
    // * use k-means to identify 2 communities in ranked graph
    // * store resulting candidate communities
    ArrayList<V> vArray = new ArrayList<V>(g.getVertices());

    LinkedList<Set<V>> candidates = new LinkedList<Set<V>>();

    for (int j = 0; j < numCandidates; j++) {
      V source;
      if (origin == null) {
        source = vArray.get((int) (rand.nextDouble() * vArray.size()));
      } else {
        source = origin;
      }
      V target = null;
      do {
        target = vArray.get((int) (rand.nextDouble() * vArray.size()));
      } while (source == target);
      VoltageScorer<V, E> vs =
          new VoltageScorer<V, E>(g, new EdgeWeightTransformer(), source, target);
      vs.evaluate();

      Map<V, double[]> voltageRanks = new HashMap<V, double[]>();
      for (V v : g.getVertices()){
        voltageRanks.put(v, new double[] {vs.getVertexScore(v)});
      }

      addTwoCandidateClusters(candidates, voltageRanks);
    }

    // repeat the following k-1 times:
    // * pick a vertex v as a cluster seed
    // (Wu/Huberman: most frequent vertex in candidates)
    // * calculate co-occurrence (in candidate clusters)
    // of this vertex with all others
    // * use k-means to separate co-occurrence counts into high/low;
    // high vertices are a cluster
    // * remove v's vertices from candidate clusters

    Collection<Set<V>> clusters = new LinkedList<Set<V>>();
    Set<V> remaining = new HashSet<V>(g.getVertices());

    List<V> seedCandidates = getSeedCandidates(candidates);
    int seedIndex = 0;

    for (int j = 0; j < (numClusters - 1); j++) {
      if (remaining.isEmpty()) {break;}

      V seed;
      if (seedIndex == 0 && origin != null) {
        seed = origin;
      } else {
        do {
          seed = seedCandidates.get(seedIndex++);
        } while (!remaining.contains(seed));
      }

      Map<V, double[]> occurCounts = getObjectCounts(candidates, seed);
      if (occurCounts.size() < 2) {break;}

      // now that we have the counts, cluster them...
      try {
        Collection<Map<V, double[]>> highLow = kmc.cluster(occurCounts, 2);
        // ...get the cluster with the highest-valued centroid...
        Iterator<Map<V, double[]>> hIter = highLow.iterator();
        Map<V, double[]> cluster1 = hIter.next();
        Map<V, double[]> cluster2 = hIter.next();
        double[] centroid1 = DiscreteDistribution.mean(cluster1.values());
        double[] centroid2 = DiscreteDistribution.mean(cluster2.values());
        Set<V> newCluster;
        if (centroid1[0] >= centroid2[0]) {
          newCluster = cluster1.keySet();
        } else {
          newCluster = cluster2.keySet();
        }

        // ...remove the elements of new_cluster from each candidate...
        for (Set<V> cluster : candidates){
          cluster.removeAll(newCluster);
        }
        clusters.add(newCluster);
        remaining.removeAll(newCluster);
      } catch (NotEnoughClustersException nece) {
        // all remaining vertices are in the same cluster
        break;
      }
    }

    // identify remaining vertices (if any) as a 'garbage' cluster
    if (!remaining.isEmpty()) {clusters.add(remaining);}

    return clusters;
  }

  /**
   * Do k-means with three intervals and pick the smaller two clusters (presumed to be on the ends);
   * this is closer to the Wu-Huberman method.
   * 
   * @param candidates
   * @param voltageRanks
   */
  protected void addTwoCandidateClusters(List<Set<V>> candidates, Map<V, double[]> voltageRanks) {
    try {
      List<Map<V, double[]>> clusters =
          new ArrayList<Map<V, double[]>>(kmc.cluster(voltageRanks, 3));
      boolean b01 = clusters.get(0).size() > clusters.get(1).size();
      boolean b02 = clusters.get(0).size() > clusters.get(2).size();
      boolean b12 = clusters.get(1).size() > clusters.get(2).size();
      if (b01 && b02) {
        candidates.add(clusters.get(1).keySet());
        candidates.add(clusters.get(2).keySet());
      } else if (!b01 && b12) {
        candidates.add(clusters.get(0).keySet());
        candidates.add(clusters.get(2).keySet());
      } else if (!b02 && !b12) {
        candidates.add(clusters.get(0).keySet());
        candidates.add(clusters.get(1).keySet());
      }
    } catch (NotEnoughClustersException e) {
      // no valid candidates, continue
    }
  }

  /**
   * alternative to addTwoCandidateClusters(): cluster vertices by voltages into 2 clusters. We only
   * consider the smaller of the two clusters returned by k-means to be a 'true' cluster candidate;
   * the other is a garbage cluster.
   * 
   * @param candidates
   * @param voltageRanks
   */
  protected void addOneCandidateCluster(LinkedList<Set<V>> candidates,
      Map<V, double[]> voltageRanks) {
    try {
      List<Map<V, double[]>> clusters;
      clusters = new ArrayList<Map<V, double[]>>(kmc.cluster(voltageRanks, 2));
      if (clusters.get(0).size() < clusters.get(1).size()) {
        candidates.add(clusters.get(0).keySet());
      } else {
        candidates.add(clusters.get(1).keySet());
      }
    } catch (NotEnoughClustersException e) {
      // no valid candidates, continue
    }
  }

  /**
   * Returns an array of cluster seeds, ranked in decreasing order of number of appearances in the
   * specified collection of candidate clusters.
   * 
   * @param candidates
   */
  protected List<V> getSeedCandidates(Collection<Set<V>> candidates) {
    final Map<V, double[]> occurCounts = getObjectCounts(candidates, null);

    ArrayList<V> occurrences = new ArrayList<V>(occurCounts.keySet());
    Collections.sort(occurrences, new MapValueArrayComparator(occurCounts));

    return occurrences;
  }

  protected Map<V, double[]> getObjectCounts(Collection<Set<V>> candidates, V seed) {
    Map<V, double[]> occurCounts = new HashMap<V, double[]>();
    for (V v : g.getVertices()){
      occurCounts.put(v, new double[] {0});
    }

    for (Set<V> candidate : candidates) {

      if (seed == null || candidate.contains(seed)) {
        for (V element : candidate) {
          double[] count = occurCounts.get(element);
          count[0]++;
        }
      }
    }

    return occurCounts;
  }

  protected class MapValueArrayComparator implements Comparator<V> {
    private Map<V, double[]> map;

    protected MapValueArrayComparator(Map<V, double[]> map) {
      this.map = map;
    }

    public int compare(V o1, V o2) {
      double[] count0 = map.get(o1);
      double[] count1 = map.get(o2);
      if (count0[0] < count1[0]) {
        return 1;
      } else if (count0[0] > count1[0]) {
        return -1;
      }
      return 0;
    }

  }

}
