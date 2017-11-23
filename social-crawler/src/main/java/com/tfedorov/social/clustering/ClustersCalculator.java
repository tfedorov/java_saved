package com.tfedorov.social.clustering;

import java.util.List;



/**
 * This interface defines methods for calculating cluster of words based on list of
 * {@link CoOccurrenceInfo} objects.
 * 
 * 
 */
public interface ClustersCalculator {

  /**
   * Calculates cluster of the words.
   * 
   * @param clustersCount - maximum cluster to output.
   * @param wordsPair list of the {@link CoOccurrenceInfo} objects
   * @return list of the {@link Cluster} objects.
   */
   List<Cluster> calculate(int clustersCount, List<CoOccurrenceInfo> wordsPair);

}
