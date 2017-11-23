package com.tfedorov.social.clustering;

import com.tfedorov.social.clustering.jgrapht.MinimumCutCalculator;
import com.tfedorov.social.clustering.jung.EdgeBetweennessCalculator;
import com.tfedorov.social.clustering.jung.VoltageClustererCalculator;


public final class ClusteringAlgorithmsFactory {

  private ClusteringAlgorithmsFactory() {
    throw new UnsupportedOperationException();
  }
  
  public static final String JGRAPHT_STOREWAGNER ="jgrapht_sw";

  public static enum ClusteringAlgorithm {
    JGRAPHT_STOREWAGNER("jgrapht_sw", "Minimum Cut"), JUNG_BETWEENNESS("jung_bw",
        "Edge Betweenness"), JUNG_VOLTAGE("jung_vc", "Voltage Clustered");

    private final String key;
    private final String displayName;

    private ClusteringAlgorithm(String key, String displayName) {
      this.key = key;
      this.displayName = displayName;
    }

    public String getKey() {
      return key;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

  public static ClustersCalculator getAlgorithm(final String aName) {
    if (aName != null && !aName.isEmpty()) {
      if (aName.equalsIgnoreCase(ClusteringAlgorithm.JGRAPHT_STOREWAGNER.getKey())) {
        return new MinimumCutCalculator();
      } else if (aName.equalsIgnoreCase(ClusteringAlgorithm.JUNG_BETWEENNESS.getKey())) {
        return new EdgeBetweennessCalculator();
      } else if (aName.equalsIgnoreCase(ClusteringAlgorithm.JUNG_VOLTAGE.getKey())) {
        return new VoltageClustererCalculator();
      } else {
        throw new IllegalArgumentException("Unknown algorithm");
      }
    } else {
      throw new IllegalArgumentException("Empty name");
    }
  }
}
