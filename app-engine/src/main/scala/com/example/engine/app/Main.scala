package com.example.engine.app

import akka.actor.ActorSystem
import com.example.engine.actor.FactorialActor
import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty)
      startup("0")
    else
      startup(args(0))
  }

  def startup(port: String): Unit = {
    // Override the configuration of the port
    val config = ConfigFactory.parseString(s"""
      akka.remote.netty.tcp.port=$port
      akka.remote.artery.canonical.port=$port
      """).withFallback(ConfigFactory.load())

    // Create an Akka system
    val system = ActorSystem("my-cluster", config)
    println(s"Cluster started at ${port}!")

    // Create an actor that listen to factorial message
    system.actorOf(FactorialActor.props, name = "factorial-actor")
  }

}
