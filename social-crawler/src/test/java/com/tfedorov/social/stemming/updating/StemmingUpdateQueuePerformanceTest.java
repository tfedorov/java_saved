package com.tfedorov.social.stemming.updating;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.tfedorov.social.normalization.stemming.StemmingResult;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

public class StemmingUpdateQueuePerformanceTest {

  private static final int STEMMING_RESULT_COUNT = 10000;
  private static final String SPACE = " ";
  private static final BatchThreadUpdater batchTreadUpdater = new BatchThreadUpdater();
  private static final ArrayBatchThreadUpdater arrayBatchTreadUpdater =
      new ArrayBatchThreadUpdater();
  private static EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();
  // therad config
  private static final int THREAD_COUNT = 1;
  private static List<Thread> threadList = new ArrayList<Thread>();
  //
  private static final int MAX_QUEUE_SIZE = 10000;
  private static StemmingResult[] queue = new StemmingResult[MAX_QUEUE_SIZE];
  private static AtomicInteger index = new AtomicInteger(0);

  public static void main(String arg[]) {
    BasicEtmConfigurator.configure(true);
    performanceMonitor.start();
    // start threads
    batchTreadUpdater.start();
    arrayBatchTreadUpdater.start();
    // random for sleep
    final Random r = new Random(10);
    // create job for all tasks thread
    Runnable job = new Runnable() {
      @Override
      public void run() {
        try {
          for (int i = 0; i < STEMMING_RESULT_COUNT; i++) {
            // TEST1
            updateTest1("word" + i, "stemmed" + i, "en");
            //
            Thread.sleep(r.nextInt(50) + 1);
            // TEST2
            updateTest2("word" + i, "stemmed" + i, "en");
            // TEST3
            System.out.println(Thread.currentThread().getName() + " - Word: " + i);
            //
            Thread.sleep(r.nextInt(100) + 1);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };

    // Initialize Task thread group
    for (int i = 0; i < THREAD_COUNT; i++) {
      Thread t = new Thread(job);
      t.setName("Thread #" + (i + 1));
      threadList.add(t);
    }
    // Run Task thread group
    for (Thread t : threadList) {
      t.start();
    }

    // wait all threads
    boolean work = true;
    while (work) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // /
      work = false;
      for (Thread t : threadList) {
        if (t.isAlive()) {
          work = true;
          break;
        }
      }
    }

    performanceMonitor.render(new SimpleTextRenderer());
    performanceMonitor.stop();
    // stop other
    batchTreadUpdater.stopWork();
    arrayBatchTreadUpdater.stopWork();
  }


  private static void updateTest1(String text, String stemmedText, String language) {
    EtmPoint p = performanceMonitor.createPoint("Test1: AllProcess");
    String trimmedText = text.trim();
    // check text for has " " and for empty and more than 3 symbols // and not equals stemmed text
    if (trimmedText.indexOf(SPACE) < 0 && !trimmedText.isEmpty() && trimmedText.length() >= 3) {
      // && !trimmedText.equals(stemmedText)) {
      // add to updater
      EtmPoint o = performanceMonitor.createPoint("AddToThreadQueue");
      batchTreadUpdater.addToQueue(new StemmingResult(trimmedText, stemmedText, language));
      o.collect();
    }
    p.collect();
  }

  private static void updateTest2(String text, String stemmedText, String language) {
    EtmPoint p = performanceMonitor.createPoint("Test2: AllProcess");
    String trimmedText = text.trim();
    // check text for has " " and for empty and more than 3 symbols // and not equals stemmed text
    if (trimmedText.indexOf(SPACE) < 0 && !trimmedText.isEmpty() && trimmedText.length() >= 3) {
      // && !trimmedText.equals(stemmedText)) {
      // add to updater
      EtmPoint o = performanceMonitor.createPoint("CheckArrayAndAddToProcess");
      if (index.get() == MAX_QUEUE_SIZE) {
        StemmingResult[] temp = queue;
        queue = new StemmingResult[MAX_QUEUE_SIZE];
        index.set(0);
        arrayBatchTreadUpdater.addToQueue(temp);
      } else {
        queue[index.getAndIncrement()] = new StemmingResult(trimmedText, stemmedText, language);
      }
      o.collect();
    }
    p.collect();
  }

  protected EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(
        StemmingUpdateQueuePerformanceTest.class.toString()).append(name).toString());
  }

}
