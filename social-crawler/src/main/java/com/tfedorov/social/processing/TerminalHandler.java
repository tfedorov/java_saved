package com.tfedorov.social.processing;

import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Terminal handler used for terminate chain processing or processing tree
 * 
 */
public class TerminalHandler<T extends ProcessingContext> implements ProcessingHandler<T> {

  public static final double MEMORY_THRESHOLD = 0.15;

  public static final long MB = 1024L * 1024L;

  private Logger logger = LoggerFactory.getLogger(TerminalHandler.class);

  private static AtomicBoolean memoryWarnNotifiedBefore = new AtomicBoolean(false);

  @Override
  public void process(T context) {

    // DO NOTHING BECAUSE OF TERMINAL HANDLER IN CHAIN

    // lets use terminal handler for free memory warning
    long mbFree = Runtime.getRuntime().freeMemory() / MB;
    long mbTotal = Runtime.getRuntime().totalMemory() / MB;

    double pFree = (double) mbFree / (double) mbTotal;
    if (pFree < MEMORY_THRESHOLD && memoryWarnNotifiedBefore.compareAndSet(false, true)) {
      logger.warn("FREE MEMORY = " + mbFree + "Mb (" + (int) (pFree * 100) + ")% !!! ");
    } else if (pFree > MEMORY_THRESHOLD) {
      memoryWarnNotifiedBefore.set(false);
    }
  }

  @Override
  public Class getClazz() {
    return TerminalHandler.class;
  }

  @Override
  public JSONObject returnJson() throws JSONException {
    JSONObject point = new JSONObject();
    point.put("cname", this.getClass().getName());
    return point;
  }
}
