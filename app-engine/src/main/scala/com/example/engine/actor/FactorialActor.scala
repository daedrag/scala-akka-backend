package com.example.engine.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import com.example.model.{Factorial, FactorialResult}
import com.example.pubsub.Destinations

import scala.annotation.tailrec

class FactorialActor extends Actor with ActorLogging {

  val mediator = DistributedPubSub(context.system).mediator
  val queueName = Destinations.FactorialQueue

  // subscribe to the queue (which group id equals to topic name)
  mediator ! Subscribe(queueName, Some(queueName), self)

  def receive = {
    case msg: Factorial => {
      log.info("Got {}", msg)
      sender() ! FactorialResult(msg.n, factorial(msg.n), msg.sender)
    }

    case SubscribeAck(Subscribe(`queueName`, None, `self`)) =>
      log.info(s"Subscribed to $queueName")
  }

  def factorial(i: Int): Long = {
    @tailrec
    def fact(i: Int, accumulator: Int): Long = {
      if (i <= 1) accumulator
      else fact(i - 1, i * accumulator)
    }

    fact(i, 1)
  }
}

object FactorialActor {
  def props = Props[FactorialActor]
}
