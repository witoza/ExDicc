package org.wito.exdicc.acca

import akka.actor.ActorSystem
import akka.actor.Props
import org.wito.exdicc.ProcessingRequest

object RunnerAcca {
  def main(args: Array[String]) {
    val system = ActorSystem("whoa")
    val executor = system.actorOf(Props[ExtractorActor], name = "myExtractor")
    val translator = system.actorOf(Props[TranslatorActor], name = "myTranslator")
    
    translator ! "bosque";

//    executor ! new ProcessingRequest("z:\\exdicc_sample.xlsx", "z:\\exdicc_sample2.xlsx")
  }
}