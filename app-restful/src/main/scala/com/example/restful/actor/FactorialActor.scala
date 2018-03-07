package com.example.restful.actor

import akka.actor.{Actor, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import com.example.model.{Factorial, FactorialResult}
import com.example.pubsub.Destinations

class FactorialActor extends Actor {

  val mediator = DistributedPubSub(context.system).mediator

  def receive: Receive = {
    case msg: Factorial => {
      val s = sender()
      val req = Factorial(msg.n, s.path.toString)
      mediator ! Publish(Destinations.FactorialQueue, req, true)
    }
    case FactorialResult(n, result, s) if s != "" => {
      val originalSender = context.system.actorSelection(s)
      originalSender ! FactorialResult(n, result)
    }
  }

}

object FactorialActor {
  def props = Props[FactorialActor]
}
