trait GreeterComponent {

  val greeter: Greeter

  trait Greeter {
    def greet(name: String): String = s"Hello $name!"
  }
}

trait ConversationStarterComponent {
  this: GreeterComponent =>

  val cStarter: ConversationStarter

  trait ConversationStarter {
    def startConversation(name: String): String = greeter.greet(name) + " How do you do?"
  }
}

object Main extends ConversationStarterComponent with GreeterComponent {
  override val greeter: Greeter = new Greeter {}
  override val cStarter: ConversationStarter = new ConversationStarter {}
}

Main.cStarter.startConversation("Richard")
