package com.tfedorov.performance.memoryinspect

import org.testng.Assert
import org.testng.annotations.Test

/**
  * Created by Taras_Fedorov on 7/25/2016.
  */
class TestMemoryPrinterUtil {

  @Test
  def testMemoryInHtmlSmoke(): Unit = {
    Assert.assertNotEquals(MemoryPrinterUtil.memoryInHtml, "", "Result should not be empty")
  }

}
