package com.tfedorov;

import java.util.concurrent.Exchanger;

/**
 * Created by Taras_Fedorov on 6/23/2016.
 */
public class ExchangerExample {
    public static void main(String[] args) throws InterruptedException {
        new ExchangerExample().doExample();
    }

    private void doExample() throws InterruptedException {
        Exchanger exchanger = new Exchanger();

        ExchangerRunnable exchangerRunnable1 =
                new ExchangerRunnable(exchanger, "A");

        ExchangerRunnable exchangerRunnable2 =
                new ExchangerRunnable(exchanger, "B");

        new Thread(exchangerRunnable1).start();
        Thread.sleep(3000);
        new Thread(exchangerRunnable2).start();
    }

    class ExchangerRunnable implements Runnable {

        Exchanger exchanger = null;
        Object object = null;

        public ExchangerRunnable(Exchanger exchanger, Object object) {
            this.exchanger = exchanger;
            this.object = object;
        }

        public void run() {
            try {
                System.out.println(
                        Thread.currentThread().getName() + " Started with field - " + object);
                Object previous = this.object;

                this.object = this.exchanger.exchange(this.object);

                System.out.println(
                        Thread.currentThread().getName() +
                                " exchanged " + previous + " for " + this.object
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
