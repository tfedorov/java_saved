package com.performance.memoryinspect;

import com.tfedorov.performance.memoryinspect.MemoryPrinterUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MemoryPrinterUtilTest extends Assert {

    @Test
    public void testMemoryInHtmlSmoke() {
        assertNotEquals(MemoryPrinterUtil.memoryInHtml(), "", "Result should not be empty");
    }


}
