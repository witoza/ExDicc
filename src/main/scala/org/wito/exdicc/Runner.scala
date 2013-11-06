package org.wito.exdicc

object Runner {
  def main(args: Array[String]) {
    val proc = new ExDiccProcessor()
    proc.process(new ProcessingRequest("z:\\exdicc_sample.xlsx", "z:\\exdicc_sample2.xlsx"))
  }
}