package com.example.app.actor

import akka.actor.{Actor, Props}
import com.example.model.{Factorial, FactorialResult}

import scala.annotation.tailrec

class FactorialActor extends Actor {

  def factorial(i: Int): Long = {
    @tailrec
    def fact(i: Int, accumulator: Int): Long = {
      if (i <= 1) accumulator
      else fact(i - 1, i * accumulator)
    }

    fact(i, 1)
  }

  def receive: Receive = {
    case x: Factorial => sender() ! FactorialResult(x.n, factorial(x.n))
    case _ => sender() ! FactorialResult(1, 1)
  }

}

object FactorialActor {
  def props = Props[FactorialActor]
}
