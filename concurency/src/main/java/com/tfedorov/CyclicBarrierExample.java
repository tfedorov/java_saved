package com.tfedorov;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by Taras_Fedorov on 6/23/2016.
 */
public class CyclicBarrierExample {

    public static void main(String[] args) {
        new CyclicBarrierExample().doBarier();
    }

    CyclicBarrier barrier1;
    CyclicBarrier barrier2;

    private void doBarier() {
        Runnable barrier1Action = new Runnable() {
            public void run() {
                System.out.println("BarrierAction 1 executed ");
            }
        };
        Runnable barrier2Action = new Runnable() {
            public void run() {
                System.out.println("BarrierAction 2 executed ");
            }
        };

        barrier1 = new CyclicBarrier(2, barrier1Action);
        barrier2 = new CyclicBarrier(2, barrier2Action);

        CyclicBarrierRunnable barrierRunnable1 =
                new CyclicBarrierRunnable();

        CyclicBarrierRunnable barrierRunnable2 =
                new CyclicBarrierRunnable();

        new Thread(barrierRunnable1).start();
        new Thread(barrierRunnable2).start();
    }

    class CyclicBarrierRunnable implements Runnable {
        /*
                CyclicBarrier barrier1 = null;
                CyclicBarrier barrier2 = null;

                public CyclicBarrierRunnable(
                        CyclicBarrier barrier1,
                        CyclicBarrier barrier2) {

                    this.barrier1 = barrier1;
                    this.barrier2 = barrier2;
                }
        */
        public void run() {
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() +
                        " waiting at barrier 1");
                barrier1.await();

                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() +
                        " waiting at barrier 2");
                barrier2.await();

                System.out.println(Thread.currentThread().getName() +
                        " done!");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

}
