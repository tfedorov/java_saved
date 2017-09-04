package com.tfedorov.forkjoinpool;

import java.util.concurrent.ForkJoinPool;

/**
 * Created by Taras_Fedorov on 6/26/2016.
 */
public class TaskExample {

    public static void main(String[] args) {
        new TaskExample().run();
    }

    private void run() {

        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        MyRecursiveTask myRecursiveTask = new MyRecursiveTask(128);

        long mergedResult = forkJoinPool.invoke(myRecursiveTask);

        System.out.println("mergedResult = " + mergedResult);
    }
}
