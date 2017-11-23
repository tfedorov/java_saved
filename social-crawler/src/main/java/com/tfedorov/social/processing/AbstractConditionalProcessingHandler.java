package com.tfedorov.social.processing;

import org.json.JSONException;
import org.json.JSONObject;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * Abstract handler that can fork processing based on template method - processImpl() return
 * 
 */
public abstract class AbstractConditionalProcessingHandler<T extends ProcessingContext>
    implements
      ProcessingHandler<T> {

  private ProcessingHandler<T> successorTrue;
  private ProcessingHandler<T> successorFalse;

  /**
   * Test purposes
   * 
   * @return the successorTrue
   */
  public ProcessingHandler<T> getSuccessorTrue() {
    return successorTrue;
  }

  /**
   * Test purposes
   * 
   * @return the successorFalse
   */
  public ProcessingHandler<T> getSuccessorFalse() {
    return successorFalse;
  }

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public AbstractConditionalProcessingHandler(ProcessingHandler<T> successorTrue,
      ProcessingHandler<T> successorFalse) {
    this.successorTrue = successorTrue;
    this.successorFalse = successorFalse;
  }

  @Override
  public void process(T context) {
    EtmPoint perfPoint = getPerformancePoint(".process()");


    try {
      boolean condition = false;
      EtmPoint perfPoint1 = getPerformancePoint(".processImpl()");
      try {
        condition = processImpl(context);
      } finally {
        perfPoint1.collect();
      }

      if (condition) {
        successorTrue.process(context);
      } else {
        successorFalse.process(context);
      }

    } finally {
      perfPoint.collect();
    }



  }

  protected abstract boolean processImpl(T context);

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(getClazz().toString()).append(name)
        .toString());
  }

  @Override
  public JSONObject returnJson() throws JSONException {
    JSONObject point = new JSONObject();
    point.put("true", successorTrue.returnJson());
    point.put("false", successorFalse.returnJson());
    point.put("cname", this.getClass().getName());
    return point;
  }
}
