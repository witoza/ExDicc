package org.wito.exdicc.acca

import akka.actor.Actor
import org.wito.exdicc.ProcessingRequest
import org.wito.exdicc.MultiThreadsProcessor

class ExtractorActor extends Actor {
  def receive = {
    case ProcessingRequest(fin, fout) =>

      val proc = new MultiThreadsProcessor()
//      proc.process(new ProcessingRequest(fin, fout), 1)

      sender ! "ACK"
  }
}