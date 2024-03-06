import SparkRecommendService.recommendTop5JobPostings
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.Dataset
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

// JSON 데이터 구조에 대응하는 케이스 클래스 정의
final case class User(name: String, age: Int)

// Spray JSON 마샬러 정의
object JsonFormats {
  // JSON 마샬링을 위해 필요한 암시적 값들을 제공
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
}

object AkkaHttpJsonExample {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpJsonSystem")
    implicit val executionContext = system.executionContext

    Logger.getLogger("org").setLevel(Level.ERROR)

    import JsonFormats._ // JSON 마샬러를 가져옵니다.

    val route = {
      path("hello") {
        get {
          complete(OK, "Hello, World!")
        }
      } ~
      path("user") {
        post {
          entity(as[User]) { user =>
            println(s"Received user: ${user.name}, ${user.age}")
            complete(user)
          }
        }
      } ~
      path("recommend") {
        get {
          parameters("userId".as[Int]) { userId =>
            println(s"Received userId: $userId")
            val recommendedJobs = recommendTop5JobPostings(userId)
            complete(OK, recommendedJobs)
          }
        }
      }
    }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/user with a POST request\nPress RETURN to stop...")
    scala.io.StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
