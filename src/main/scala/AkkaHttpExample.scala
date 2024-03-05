import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.ContentTypes.`text/html(UTF-8)`

object AkkaHttpExample {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpSystem")
    implicit val executionContext = system.executionContext

    val route =
      path("hello") {
        get {
          complete(HttpResponse(OK, entity = "Hello, World!"))
        }
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    scala.io.StdIn.readLine() // 사용자가 ENTER를 누를 때까지 대기합니다.
    bindingFuture
      .flatMap(_.unbind()) // 포트 바인딩 해제
      .onComplete(_ => system.terminate()) // 액터 시스템 종료
  }
}