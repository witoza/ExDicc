package org.wito.exdicc.acca

import akka.actor.Actor
import org.wito.exdicc.SpanishDict

class TranslatorActor extends Actor {

  def receive = {
    case word: String =>
      sender ! SpanishDict(word);
  }

}