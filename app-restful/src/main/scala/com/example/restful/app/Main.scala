package com.example.restful.app

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.example.restful.actor.FactorialActor
import com.example.model.{Factorial, FactorialResult, Greeting}
import com.typesafe.config.ConfigFactory
import spray.json.{DefaultJsonProtocol, PrettyPrinter}

import scala.concurrent.duration._
import scala.io.StdIn

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val greetingFormat = jsonFormat1(Greeting)
  implicit val factorialFormat = jsonFormat2(Factorial)
  implicit val factorialResultFormat = jsonFormat3(FactorialResult)
}

trait PrettyPrinterSupport {
  implicit val prettyPrinter = PrettyPrinter
}

object Main extends App with JsonSupport with PrettyPrinterSupport {

  implicit val system       = ActorSystem("my-cluster", ConfigFactory.load())
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  import akka.pattern.ask

  val factorialActor = system.actorOf(FactorialActor.props, "factorial-actor")

  val route = concat(
    path("hello") {
      get {
        val greeting = Greeting("User")
        complete(greeting)
      }
    },
    path("hello" / Remaining) { name =>
      get {
        val greeting = Greeting(name)
        complete(greeting)
      }
    },
    path("factorial" / IntNumber) { n =>
      get {
        implicit val askTimeout: Timeout = 3.seconds // and a timeout

        onSuccess((factorialActor ? Factorial(n)).mapTo[FactorialResult]) { result =>
          complete(result)
        }
      }
    }
  )

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
