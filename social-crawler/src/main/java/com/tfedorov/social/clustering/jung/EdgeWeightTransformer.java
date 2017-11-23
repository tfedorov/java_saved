package com.tfedorov.social.clustering.jung;
import org.apache.commons.collections15.Transformer;


/**
 * This class transforms {@link WeightedEdge} object to double value.
 * 
 * 
 * @param <T>
 */
public class EdgeWeightTransformer<T> implements Transformer<WeightedEdge, Double> {


  public Double transform(WeightedEdge edge) {
    return edge.getWeight();
  }



}
