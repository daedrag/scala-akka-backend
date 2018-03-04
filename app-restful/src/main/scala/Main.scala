import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json.{DefaultJsonProtocol, PrettyPrinter}

import scala.io.StdIn

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val greetingFormat = jsonFormat1(Greeting)
}

trait PrettyPrinterSupport {
  implicit val prettyPrinter = PrettyPrinter
}

object Main extends App with JsonSupport with PrettyPrinterSupport {
  implicit val system       = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val route =
    path("hello") {
      get {
        val greeting = Greeting("User")
        complete(greeting)
      }
    } ~
    path("hello" / String) { name => {
      get {
        val greeting = Greeting("User")
        complete(greeting)
      }
    } }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}