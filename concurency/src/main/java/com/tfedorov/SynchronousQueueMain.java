package com.tfedorov;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Taras_Fedorov on 6/22/2016.
 */
public class SynchronousQueueMain {
    public static void main(String[] args) throws InterruptedException {
        //BlockingQueue queue = new ArrayBlockingQueue(1024);
        BlockingQueue queue = new SynchronousQueue();


        Producer producer = new Producer(queue);
        Producer producer2 = new Producer(queue);

        Consumer consumer = new Consumer(queue);

        new Thread(producer).start();
        //new Thread(producer2).start();
        Thread.sleep(3000);
        new Thread(consumer).start();


        System.out.println("Time out");
        //Thread.sleep(16000);
        System.out.println("Final Queu size = " + queue.size());
    }

    static class Producer implements Runnable {

        protected BlockingQueue queue = null;

        public Producer(BlockingQueue queue) {
            this.queue = queue;
        }

        public void run() {
            System.out.println("Producer start " + System.identityHashCode(this));
            try {
                for (int i = 1; i <= 3; i++) {
                    String toPut = i + " " + System.identityHashCode(this);

                    System.out.println("Try to put");
                    queue.put(toPut);
                    System.out.println("Put = \"" + toPut + "\"");

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
                System.out.println("Consumer start, queue size = " + queue.size());
                while (true) {
                    System.out.println("Take =  \"" + queue.take() + "\"");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
