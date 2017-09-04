package com.tfedorov.performance.memoryinspect

import org.testng.Assert
import org.testng.annotations.Test

/**
  * Created by Taras_Fedorov on 7/27/2016.
  */
class TestMemoryUtil {

  @Test
  def testMemoryInHtmlSmoke(): Unit = {
    MemoryUtil.startGCMonitor()

    MemoryUtil.printUsage(false)
    MemoryUtil.stopGCMonitor()
  }
}
