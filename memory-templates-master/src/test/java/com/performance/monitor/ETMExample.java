package com.performance.monitor;

import com.tfedorov.performance.monitor.PerformMonitor;
import etm.core.monitor.EtmPoint;

public class ETMExample {

    public static void main(String[] args) {
        PerformMonitor.start();
        PerformMonitor.runConsole();
        ETMExample obj = new ETMExample();
        obj.toTest("somw");
    }

    public String toTest(String inputName) {
        EtmPoint perfPoint = PerformMonitor.createPoint(className());
        try {
            return inputName + inputName;
        } finally {
            perfPoint.collect();
        }
    }

    private String className() {
        return this.getClass().toString();
    }
}
