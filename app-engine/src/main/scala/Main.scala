import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props

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
    val system = ActorSystem("ClusterSystem", config)
    println(s"Cluster started at ${port}!")

    // Create an actor that handles cluster domain events
    // system.actorOf(Props[SimpleClusterListener], name = "clusterListener")
  }

}
