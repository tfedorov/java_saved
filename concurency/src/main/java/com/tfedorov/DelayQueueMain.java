package com.tfedorov;

import java.util.concurrent.*;

/**
 * Created by Taras_Fedorov on 6/22/2016.
 */
public class DelayQueueMain {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue queue = new DelayQueue();

        Producer producer = new Producer(queue);
        Producer producer2 = new Producer(queue);
        Consumer consumer = new Consumer(queue);

        new Thread(producer).start();
        new Thread(producer2).start();

        System.out.println("Consumer start " );

        Thread.sleep(8000);
        new Thread(consumer).start();

        Thread.sleep(8000);
        System.out.println("Final Queue size = " + queue.size());

    }

    static class Producer implements Runnable {

        protected BlockingQueue queue = null;

        public Producer(BlockingQueue queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                for (Integer i = 1; i <= 3; i++) {

                    DelayedElement delayedElement = new DelayedElement(i.toString());

                    System.out.println("Put = " + delayedElement);
                    queue.put(delayedElement);
                    Thread.sleep(1000);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer implements Runnable {

        protected BlockingQueue queue = null;

        public Consumer(BlockingQueue queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                System.out.println("Consumer start Queue size = " + queue.size());
                while (queue.size() > 0) {
                    System.out.println("Take = " + queue.take());
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class DelayedElement implements Delayed {
        private String keyId;

        public DelayedElement(String key) {
            keyId = key + " " + System.identityHashCode(this);
        }

        public long getDelay(TimeUnit unit) {
            return 100;
        }

        public int compareTo(Delayed o) {
            return 1000;
        }

        @Override
        public String toString() {
            return "DelayedElement{" +
                    "keyId='" + keyId + '\'' +
                    '}';
        }
    }
}
