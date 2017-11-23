package com.tfedorov.social.processing;

import org.json.JSONException;
import org.json.JSONObject;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * Base handler that implements general logic of chain - process data in context - delegate
 * processing to successor - next handler in chain
 * 
 */
public abstract class AbstractChainProcessingHandler<T extends ProcessingContext>
    implements
      ProcessingHandler<T> {

  private ProcessingHandler<T> successor;

  /**
   * Test purposes
   * 
   * @return the successor
   */
  public ProcessingHandler<T> getSuccessor() {
    return successor;
  }

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public AbstractChainProcessingHandler(ProcessingHandler<T> successor) {
    this.successor = successor;
  }

  @Override
  public void process(T context) {
    EtmPoint perfPoint = getPerformancePoint(".process()");
    try {

      EtmPoint perfPoint1 = getPerformancePoint(".processImpl()");
      try {
        processImpl(context);
      } finally {
        perfPoint1.collect();
      }

      successor.process(context);
    } finally {
      perfPoint.collect();
    }
  }

  protected abstract void processImpl(T context);


  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(getClazz().toString()).append(name)
        .toString());
  }

  @Override
  public JSONObject returnJson() throws JSONException {
    JSONObject point = new JSONObject();
    point.put("cname", this.getClass().getName());
    point.put("next", successor.returnJson());
    return point;
  }
}
