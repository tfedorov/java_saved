package com.tfedorov.performance.monitor;

import etm.contrib.console.HttpConsoleServer;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

public class PerformMonitor {

    private static EtmMonitor currentMonitor = EtmManager.getEtmMonitor();

    public static void start() {
        BasicEtmConfigurator.configure();
        currentMonitor.start();
    }

    public static void stop() {
        currentMonitor.stop();
    }

    public static void runConsole() {
        HttpConsoleServer server = new HttpConsoleServer(currentMonitor);
        server.setListenPort(45000);
        server.start();
    }

    public static void simplePrint() {
        currentMonitor.render(new SimpleTextRenderer());
    }

    public static EtmPoint createPoint(String input) {
        return currentMonitor.createPoint(input);
    }


}
