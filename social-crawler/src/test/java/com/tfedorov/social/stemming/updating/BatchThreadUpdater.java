package com.tfedorov.social.stemming.updating;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.tfedorov.social.normalization.stemming.StemmingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tfedorov.social.normalization.stemming.StemmingServiceImpl;
import com.tfedorov.social.normalization.stemming.StemmingUpdateThread;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

public class BatchThreadUpdater extends Thread {

  private static final int MAX_QUEUE_SIZE = 50000;
  private BlockingQueue<StemmingResult> queue = new ArrayBlockingQueue<StemmingResult>(
      MAX_QUEUE_SIZE);
  private volatile AtomicBoolean work = new AtomicBoolean(false);

  private EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  private Logger logger = LoggerFactory.getLogger(StemmingServiceImpl.class);

  public BatchThreadUpdater() {
    setName("stemmingUpdaterThread");
  }


  @Override
  public void run() {
    work.set(true);
    //
    while (work.get()) {
      if (queue.isEmpty()) {
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          logger.error("Error on stemming DB update thread!", e);
        }
      } else {
        List<StemmingResult> updateList = new ArrayList<StemmingResult>();
        for (int i = 0; i < 1000; i++) {
          if (!queue.isEmpty()) {
            updateList.add(queue.poll());
          } else {
            break;
          }
        }
        // Update on DB
      }
    }
  }


  public void stopWork() {
    work.set(false);
  }

  public boolean isWork() {
    return work.get();
  }

  public void addToQueue(StemmingResult st) {
    if (queue.size() < MAX_QUEUE_SIZE) {
      queue.add(st);
    } else {
      logger.warn("Stemming update QUEUE out of limit : " + MAX_QUEUE_SIZE
          + "! New element don't add!");
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
