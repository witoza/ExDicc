package org.wito.exdicc.acca

import akka.actor.Actor
import org.wito.exdicc.ExDiccProcessor
import org.wito.exdicc.ProcessingRequest

class ExtractorActor extends Actor {
  def receive = {
    case ProcessingRequest(fin, fout) =>

      val proc = new ExDiccProcessor()
      proc.process(new ProcessingRequest(fin, fout))

      sender ! "ACK"
  }
}