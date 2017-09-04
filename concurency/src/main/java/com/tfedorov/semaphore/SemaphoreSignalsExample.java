package com.tfedorov.semaphore;

import java.util.concurrent.Semaphore;

/**
 * Created by Taras_Fedorov on 6/26/2016.
 */
public class SemaphoreSignalsExample {

    public static void main(String[] args) throws InterruptedException {
        new SemaphoreSignalsExample().run();
    }

    private Semaphore semaphore;

    private void run() throws InterruptedException {
        semaphore = new Semaphore(1);

        new Thread(new Sender(semaphore)).start();
        new Thread(new Receiver(semaphore)).start();
    }

    class Sender implements Runnable {

        private final Semaphore semaphore;

        Sender(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                unsafeRun();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void unsafeRun() throws InterruptedException {
            System.out.println("---Sender started:");

            semaphore.acquire();

            Thread.sleep(4000);

            System.out.println("---Sender going to send signal");

            semaphore.release();

            Thread.sleep(5000);
            System.out.println("---Sender going to send 2 signal");

            semaphore.release();

            System.out.println("---Sender is finished");

        }
    }


    class Receiver implements Runnable {

        private final Semaphore semaphore;

        Receiver(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                unsafeRun();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void unsafeRun() throws InterruptedException {
            System.out.println("<<<Receiver started:");

            Thread.sleep(2000);
            System.out.println("<<<Receiver has NOT got a signal");

            Thread.sleep(500);

            semaphore.acquire();

            System.out.println("<<<Receiver has got a signal");

            Thread.sleep(500);

            semaphore.acquire();

            System.out.println("<<<Receiver has got a 2 signal");


            System.out.println("<<<Receiver is finished");

        }
    }
}
