package org.wito.exdicc

object TranslateAndPublish {
  def main(args: Array[String]) {
    val proc = new MultiThreadsProcessor
    proc.process(new ProcessingRequest("z:\\exdicc_spanish1.xlsx"), 10)
    
    val amp = new ToAnyMemoFormatProcessor
    amp.process(new ProcessingRequest("z:\\exdicc_spanish1.xlsx", "z:\\exdicc_spanish1_anymemo.xlsx"))
  }
}