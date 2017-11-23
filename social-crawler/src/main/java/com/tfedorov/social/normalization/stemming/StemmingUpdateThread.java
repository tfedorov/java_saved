package com.tfedorov.social.normalization.stemming;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

public abstract class StemmingUpdateThread extends Thread {
  public static final int THREAD_SLEEP_TIME = 10000;
  private static final int MAX_QUEUE_SIZE = 500;
  private static final int MAX_COUNT_OF_LIMIT_LOGGING = 100;
  private BlockingQueue<StemmingResult[]> queue = new ArrayBlockingQueue<StemmingResult[]>(
      MAX_QUEUE_SIZE);
  private volatile AtomicBoolean work = new AtomicBoolean(false);

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private Logger logger = LoggerFactory.getLogger(StemmingUpdateThread.class);

  private int countOfLimitLogging = 0;

  public StemmingUpdateThread() {
    setName("stemmingUpdaterThread");
  }


  @Override
  public void run() {
    work.set(true);
    //
    while (work.get()) {
      StemmingResult[] items = queue.poll();
      if (items != null) {
        update(items);
      } else {
        try {
          Thread.sleep(THREAD_SLEEP_TIME);
        } catch (InterruptedException e) {
          logger.error("Error on stemming DB update thread!", e);
        }
      }
    }
    logger.warn("Stemming updater thread work off!");
  }

  public abstract void update(StemmingResult[] stemingResults);

  public void stopWork() {
    work.set(false);
  }

  public boolean isWork() {
    return work.get();
  }

  public void addToQueue(StemmingResult[] st) {
    if (queue.size() < MAX_QUEUE_SIZE) {
      queue.add(st);
    } else {
      if (countOfLimitLogging++ == MAX_COUNT_OF_LIMIT_LOGGING) {
        logger.warn("Stemming update QUEUE out of limit : " + MAX_QUEUE_SIZE
            + "! New element don't add!");
        //
        countOfLimitLogging = 0;
      }
    }
  }

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(StemmingUpdateThread.class.toString())
        .append(name).toString());
  }


  public Integer getQueueSize() {
    return queue.size();
  }

}
