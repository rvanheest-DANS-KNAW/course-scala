// a human can listen to you
// BTW case classes in this example are just there for prettyprinting ;)
case class Human(val name: String) {
  def listen() = s"I ($name) am listening"
}

val alice = new Human("Alice")
alice.listen()
