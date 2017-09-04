package com.tfedorov.forkjoinpool;

import java.util.concurrent.ForkJoinPool;

/**
 * Created by Taras_Fedorov on 6/26/2016.
 */
public class ActionExample {
    public static void main(String[] args) {
        new ActionExample().run();
    }

    private void run() {
        ForkJoinPool forkJoinPool = new ForkJoinPool(6);
        MyRecursiveAction myRecursiveAction = new MyRecursiveAction(48);

        forkJoinPool.invoke(myRecursiveAction);
    }
}
