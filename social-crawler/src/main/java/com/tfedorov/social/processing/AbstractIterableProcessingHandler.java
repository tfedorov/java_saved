package com.tfedorov.social.processing;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * Abstract handler that iterates over List<T> and delegate processing to repeatedSucessor After
 * finishing delegate processing to exitSuccessor
 * 
 */
public abstract class AbstractIterableProcessingHandler<P extends ProcessingContext, Q extends ProcessingContext>
    implements
      ProcessingHandler<Q> {


  private ProcessingHandler<Q> repeatedSucessor;

  private ProcessingHandler<Q> exitSuccessor;

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  public AbstractIterableProcessingHandler(ProcessingHandler<Q> repeatedSucessor,
      ProcessingHandler<Q> exitSuccessor) {
    this.repeatedSucessor = repeatedSucessor;
    this.exitSuccessor = exitSuccessor;
  }

  @Override
  public void process(Q generalContext) {

    List<P> list = getContextsList(generalContext);

    EtmPoint perfPoint = getPerformancePoint(".process()");
    try {
      for (P subContext : list) {
        EtmPoint perfPoint1 = getPerformancePoint(".prepareSubContext()");
        try {
          generalContext.add(subContext.getContextName(), subContext);
        } finally {
          perfPoint1.collect();
        }
        repeatedSucessor.process(generalContext);
      }
      exitSuccessor.process(generalContext);
    } finally {
      perfPoint.collect();
    }

  }

  protected abstract List<P> getContextsList(Q processingContext);

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(getClazz().toString()).append(name)
        .toString());
  }

  @Override
  public JSONObject returnJson() throws JSONException {
    JSONObject point = new JSONObject();
    point.put("cname", this.getClass().getName());
    point.put("repeat", repeatedSucessor.returnJson());
    point.put("quit", exitSuccessor.returnJson());

    return point;
  }

}
