package com.tfedorov.social.clustering.jung;

/**
 * This class is object representation for weighted edge.
 * 
 * 
 */
public class WeightedEdge {

  private final double weight;
  private final String target;
  private final String source;

  public WeightedEdge(double weight, String source, String target) {
    this.weight = weight;
    this.source = source;
    this.target = target;
  }

  @Override
  public String toString() {
    return "SocialWeightedEdge [source=" + source + ", target=" + target + ", weight=" + weight
        + "]";
  }

  public double getWeight() {
    return weight;
  }

  public String getTarget() {
    return target;
  }

  public String getSource() {
    return source;
  }



}