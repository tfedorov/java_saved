package com.tfedorov.social.clustering.jgrapht;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * This edge class has public getters for target , source and weight fields.
 * 
 * 
 */
public class SocialWeightedEdge extends DefaultWeightedEdge {


  private static final long serialVersionUID = 1L;

  public SocialWeightedEdge() {
    super();

  }

  @Override
  public String toString() {
    return getWeight() + "";
  }

  public String toDetailedString() {
    return super.toString() + " - " + getWeight();
  }

  @Override
  public Object getSource() {
    return super.getSource();
  }

  @Override
  protected Object getTarget() {
    return super.getTarget();
  }

  @Override
  public double getWeight() {
    return super.getWeight();
  }
}
