package com.tfedorov.performance

import org.jhades.JHades

/**
  * Created by Taras_Fedorov on 7/28/2016.
  */
object TestJarHell extends App {

  val result = new JHades()
    .dumpClassloaderInfo()
    .printClasspath()
    .overlappingJarsReport()
    .multipleClassVersionsReport()

}
