import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior

// 메시지를 정의합니다.
final case class Greet(whom: String)

// Greeter 액터를 정의합니다.
object Greeter {
  def apply(): Behavior[Greet] =
    Behaviors.receive { (context, message) =>
      println(s"Hello ${message.whom}!")
      Behaviors.same
    }
}

// 메인 애플리케이션을 정의합니다.
object HelloAkka {
  def main(args: Array[String]): Unit = {
    // 액터 시스템을 생성합니다.
    val greeter: ActorSystem[Greet] = ActorSystem(Greeter(), "helloAkka")

    // 액터에게 메시지를 보냅니다.
    greeter ! Greet("Akka") // 'replyTo' 파라미터로 'greeter'의 ActorRef를 전달합니다.
  }
}
