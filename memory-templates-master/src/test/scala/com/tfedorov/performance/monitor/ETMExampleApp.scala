package com.tfedorov.performance.monitor

import com.performance.monitor.ETMExample

/**
  * Created by Taras_Fedorov on 7/25/2016.
  */
object ETMExampleApp extends App {

  PerformMonitor.start()
  PerformMonitor.runConsole()
  val obj: ETMExample = new ETMExample
  obj.toTest("somw")

  def toTest(inputName: String): String = {
    val perfPoint = PerformMonitor.createPoint(className)
    try
      inputName + inputName
    finally perfPoint.collect()
  }

  private def className: String = this.getClass.toString

}
