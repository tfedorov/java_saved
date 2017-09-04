package com.tfedorov;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by Taras_Fedorov on 6/25/2016.
 */
public class ExecutorServiceExample {

    public static void main(String[] args) throws Exception {
        new ExecutorServiceExample().run();
    }

    private void run() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        new Thread(new ExecutorRunnable(executorService)).start();
        Thread.sleep(1000);

        System.out.println("Before shut down");

        executorService.shutdown();

        executorService.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("After shut down");

    }

    class ExecutorRunnable implements Runnable {

        private final ExecutorService executorService;

        public ExecutorRunnable(ExecutorService executorService) {
            this.executorService = executorService;
        }

        public void run() {
            Set<Callable<String>> callables = new HashSet<Callable<String>>();

            callables.add(new Callable<String>() {
                public String call() throws Exception {

                    System.out.println("Task 1");
                    Thread.sleep(10000);
                    System.out.println("Still Task 1");
                    return "Task 1";
                }
            });
            callables.add(new Callable<String>() {
                public String call() throws Exception {
                    System.out.println("Task 2");
                    Thread.sleep(10000);
                    System.out.println("Still Task 2");
                    return "Task 2";
                }
            });
            callables.add(new Callable<String>() {
                public String call() throws Exception {
                    System.out.println("Task 3");
                    Thread.sleep(10000);
                    System.out.println("Still Task 3");
                    return "Task 3";
                }
            });

            try {

                System.out.println("Before invoke all");
                executorService.invokeAll(callables);

                System.out.println("After invoke all");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
