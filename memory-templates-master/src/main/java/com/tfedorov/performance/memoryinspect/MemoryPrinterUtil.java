package com.tfedorov.performance.memoryinspect;

import java.text.MessageFormat;

/**
 * Created by Taras_Fedorov on 7/25/2016.
 */
public class MemoryPrinterUtil {

    private static final int MEMORY_SIZE = 1024;

    public static String memoryInHtml() {
        long mbFree = Runtime.getRuntime().freeMemory() / (MEMORY_SIZE * MEMORY_SIZE);
        long mbTotal = Runtime.getRuntime().totalMemory() / (MEMORY_SIZE * MEMORY_SIZE);
        long mbMax = Runtime.getRuntime().maxMemory() / (MEMORY_SIZE * MEMORY_SIZE);
        double pFree = (double) mbFree / (double) mbTotal;

        Object[] mArgFM = {pFree * 100};
        String freePerc = MessageFormat.format("({0,number,integer}%)", mArgFM);
        if (pFree < 0.2) {
            freePerc =
                    MessageFormat.format("<font color=\"red\"> ({0,number,integer}% !!!) </font>", mArgFM);
        } else if (pFree < 0.3) {
            freePerc =
                    MessageFormat.format("<font color=\"orange\"> ({0,number,integer}% !!!) </font>", mArgFM);
        }

        Object[] mArg = {mbFree, freePerc, mbTotal, mbMax};
        String formatedMessage = MessageFormat.format(
                "{0,number,integer}Mb {1} / {2,number,integer}Mb / {3,number,integer}Mb", mArg);
        return formatedMessage;
    }

}
