package com.tfedorov.social.clustering;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tfedorov.social.clustering.exception.ClusterValidationException;
import org.slf4j.Logger;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * This class contains common logic like performance monitoring , input parameters checking and
 * logging for all words cluster calculators.
 * 
 * 
 */
public abstract class AbstractClusterCalculator implements ClustersCalculator {

  private static final String ERROR_MESSAGE = "No enough data!";

  private int clustersCount;

  private List<CoOccurrenceInfo> wordsPair;

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public AbstractClusterCalculator() {
    super();
  }

  public final List<Cluster> calculate(int clustersCount, List<CoOccurrenceInfo> wordsPair) {

    EtmPoint perfPoint = getPerformancePoint(".calculate()");
    List<Cluster> clusters = Collections.emptyList();

    try {

      this.clustersCount = clustersCount;
      this.wordsPair = wordsPair;

      if (clustersCount <= 0) {

        throw new ClusterValidationException("Incorrect numebr of clusters requested:"
            + clustersCount);

      } else {

        build();
        clusters = doCalculate();
      }

    } finally {
      perfPoint.collect();
    }

    return clusters;
  }

  private List<Cluster> doCalculate() {

    checkClusterWordsPair();

    if (checkVertextClusterCount()) {
      return calculateDefault();
    }

    return calculate();
  }

  protected final List<Cluster> calculateDefault() {
    if (wordsPair != null) {
      final List<Cluster> clusters = new ArrayList<Cluster>();
      final Set<String> uniqueWords = new HashSet<String>();
      for (final CoOccurrenceInfo pair : wordsPair) {
        if (!uniqueWords.contains(pair.getFirstWord())) {
          uniqueWords.add(pair.getFirstWord());
          clusters.add(new Cluster(Collections.singleton(pair.getFirstWord())));
        }
        if (!uniqueWords.contains(pair.getSecondWord())) {
          uniqueWords.add(pair.getSecondWord());
          clusters.add(new Cluster(Collections.singleton(pair.getSecondWord())));
        }
      }
      return Collections.unmodifiableList(clusters);
    }
    return Collections.emptyList();
  }
  
  public int getClustersCount() {
    return clustersCount;
  }

  public void setClustersCount(int clustersCount) {
    this.clustersCount = clustersCount;
  }

  public List<CoOccurrenceInfo> getWordsPair() {
    return wordsPair;
  }

  public void setWordsPair(List<CoOccurrenceInfo> wordsPair) {
    this.wordsPair = wordsPair;
  }

  protected abstract void build();

  protected abstract List<Cluster> calculate();

  protected abstract boolean checkVertextClusterCount();

  protected abstract Logger getLogger();

  private void checkClusterWordsPair() {
    if (wordsPair == null || wordsPair.isEmpty()) {
      throw new ClusterValidationException(ERROR_MESSAGE);
    }
  }

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(getClass().toString()).append(name)
        .toString());
  }
}
