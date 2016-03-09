object IT1 {
  // a human can listen to you
  class Human(val name: String) {
    def listen() = {
      println(s"I ($name) am listening")
    }
  }

  val alice = new Human("Alice")
  alice.listen()
}

object IT2 {

  // more general, a friend can listen to you
  trait Friend {
    // to use the name in listen(), you need to declare it here and give a value for it in each implementation
    val name: String

    def listen() = {
      println(s"I ($name) am listening")
    }
  }

  // a human can be a friend
  class Human(val name: String) extends Friend

  // declaration for any animal
  class Animal(val name: String)

  // a dog is an animal
  // notice the override here, why do we need it here?
  class Dog(override val name: String) extends Animal(name)

  // a Human, 'Alice', can listen
  val alice = new Human("Alice")
  alice.listen()

  // a Dog, "Maurice", cannot listen because neither Dog or its super class have a method `listen()`
  val maurice = new Dog("Maurice")
//maurice.listen()
}

object IT3 {
  trait Friend {
    val name: String

    def listen() = {
      println(s"I ($name) am listening")
    }
  }

  class Human(val name: String) extends Friend

  class Animal(val name: String)

  // a Dog is now declared as both an Animal and a Friend
  // notice that we use `with` here: only the first extension gets `extends`, the rest gets `with`
  class Dog(override val name: String) extends Animal(name) with Friend

  val alice = new Human("Alice")
  alice.listen()

  // the Dog, "Maurice", can now listen because Dog now inherits from Friend
  val maurice = new Dog("Maurice")
  maurice.listen()
}

object IT4 {
  trait Friend {
    val name: String

    def listen() = {
      println(s"I ($name) am listening")
    }
  }

  class Human(val name: String) extends Friend

  class Animal(val name: String)

  class Dog(override val name: String) extends Animal(name) with Friend

  // a Cat is an animal
  class Cat(override val name: String) extends Animal(name)

  val alice = new Human("Alice")
  alice.listen()

  val maurice = new Dog("Maurice")
  maurice.listen()

  // a Cat, "Octa", cannot listen because neither Cat or its super class have a method `listen()`
  val octa = new Cat("Octa")
  //octa.listen()

  // another Cat, "Tom", can however listen, because it has the Friend inheritance on its instance
  val tom = new Cat("Tom") with Friend
  tom.listen()
}

object IT5 {
  trait Friend {
    val name: String

    def listen() = {
      println(s"I ($name) am listening")
    }
  }

  class Human(val name: String) extends Friend

  class Animal(val name: String)

  class Dog(override val name: String) extends Animal(name) with Friend

  class Cat(override val name: String) extends Animal(name)

  // you can seek help from a Friend
  def seekHelp(friend: Friend) = {
    friend.listen()
  }

  val alice = new Human("Alice")
  val maurice = new Dog("Maurice")
  val tom = new Cat("Tom") with Friend

  // you can seek for help from Alice, Maurice and Tom
  seekHelp(alice)
  seekHelp(maurice)
  seekHelp(tom)
  // Octa isn't a Friend however, so you cannot seek help there
//seekHelp(new Cat("Octa"))
}
