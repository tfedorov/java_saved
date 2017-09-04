package com.tfedorov.semaphore;

import java.util.concurrent.Semaphore;

/**
 * Created by Taras_Fedorov on 6/24/2016.
 */
public class SemaphoreExample {

    public static void main(String[] args) {
        new SemaphoreExample().execute();
    }

    private void execute() {
        Semaphore semaphore1 = new Semaphore(1);
        Semaphore semaphore2 = new Semaphore(2);

        new Thread(new SemaphoreRunnable(semaphore1, semaphore2)).start();
        new Thread(new SemaphoreRunnable(semaphore1, semaphore2)).start();
        new Thread(new SemaphoreRunnable(semaphore1, semaphore2)).start();

    }


    class SemaphoreRunnable implements Runnable {

        private final Semaphore semaphore1;

        private final Semaphore semaphore2;


        public SemaphoreRunnable(Semaphore semaphore1, Semaphore semaphore2) {
            this.semaphore1 = semaphore1;
            this.semaphore2 = semaphore2;

        }

        public void run() {

            try {
                unsafeRun();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void unsafeRun() throws InterruptedException {
            while (true) {

                semaphore1.acquire();
                System.out.println("Start execute unit " + selfID());

                printInterestingeMsg();
                System.out.println("Finish execute unit " + selfID());

                semaphore1.release();
                Thread.sleep(1000);
            }

        }

        private void printInterestingeMsg() throws InterruptedException {

            System.out.println("Do intresting " + selfID());
            Thread.sleep(2000);

            //semaphore1.acquire();
            for (int i = 0; i < 4; i++) {
                System.out.print(i);
                Thread.sleep(1000);
            }

            System.out.println(4);
            //semaphore1.release();

        }

        private String selfID() {
            return Thread.currentThread().getName();
        }

    }

}

