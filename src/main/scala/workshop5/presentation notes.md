Cake Pattern
============


Why cake pattern?
-----------------

- modularity
    - usually modules are packages
    - packages are just 'groups of files'
    - packages cannot be composed
    - traits can be seen as modules as well
    - 'traits as modules' ...
        - ... can be composed
        - ... give explicit typechecked dependencies
        - ... give complete encapsulation: not only method implementations can be hidden, but also type implementations
        - ... allow to look from the outside and see all dependencies
        - ... is basically 'OOP: The Good Parts'
- well-defined modules allow for ...
    - ... better testing
    - ... better code-reuse


General overview
----------------

- Software Design Pattern
- Dependency Injection; but typechecked!
- No frameworks/dependencies
- Few resources on the web, so invent and interpret what works best yourself


What is the cake pattern?
-------------------------

2 layers:
- interface/implementation
- wiring

A module exists of the actual thing you're making (`Greeter` below) as a trait inside a trait.
The `xxxComponent` is only a _container_ of what you want to inject in the application.

Below, the `GreeterComponent` contains a trait `Greeter`, containing some kind of functionality,
as well as a `val greeter: Greeter`, which is the thing we want to inject (the _access point_).

```scala
trait GreeterComponent {
  
  val greeter: Greeter
  
  trait Greeter {
    // methods and such in here
  }
}
```

**Make sure you only have one access point per component.** If not, you might end up with components
that contain multiple pieces that need to be wired up and loose track of which you have wired up and
which ones you have not wired.

Wiring is done by extending from the set of components and assigning the access points.

```scala
object GreeterWiring extends GreeterComponent {

  val greeter = new Greeter {}
}
```


Dependency injection with traits
--------------------------------

There are two basic ways to compose traits in Scala.

### using inheritance

```scala
trait A
trait B extends A
trait C extends B
```

Now `B` can access all methods from `A`, and `C` can access all methods from both `B` and `A`.
Usually you only use this when _`B` **is** an `A`_ and _`C` **is** a `B`_.

To create an instance of type `C`, you just write:

```scala
val c: C = new C {}
```

### using self-type annotations

```scala
trait A
trait B { this: A => }
trait C { this: B => }
```

The `this: A =>` denotes a dependency of `B` on `A`, like _`B` requires `A`_.
Now `B` can still access all methods from `A`, but `C` can only access the methods from `B`.
Since `C` does not require `A`, it cannot access the methods in `A`.

While creating an instance of type `C`, you have to give all its dependencies as well:

```scala
val c: C = new C with B with A {}
```

### example

Below we create a simple application that uses user data from a database. The `Database` implements
generic methods for communicating with the database, while the `UserDB` contains methods to
specifically query the user data. An `EmailService` uses the `UserDB` to find email addresses.

When using inheritance as the standard way of composition, we find that the `EmailService` can now
call the generic `Database` methods by itself. This is not a proper way of composition, since an
`EmailService` **is not** a `Database` nor a `UserDB`. It rather **requires** a `UserDB`, which
in turn **requires** a `Database`.

```scala
trait Database {
  def query(/* parameters */): Any = ???
}
trait UserDB extends Database {
  def getUserData(/* parameters */): Any = ???
}
trait EmailService extends UserDB {
  // Has access to all the Database methods
  // when it only should just be able to talk to the UserDb abstraction
}

val emailService = new EmailService {}
```

This is why self-type annotations are a better way to compose these traits. Here the `EmailService`
can only access the methods found in `UserDB`, but cannot call the methods declared in `Database`.
If the `EmailService` needs to call the methods on `Database`, it has to explicitly declare this
dependency: `trait EmailService { this: UserDB with Database =>`.

```scala
trait Database {
  def query(/* parameters */): Any = ???
}
trait UserDB { this: Database =>
  def getUserData(/* parameters */): Any = ???
}
trait EmailService { this: UserDB =>
  // Can only access UserDb methods, cannot access Database methods
  val userData = getUserData()
}

val emailService = new EmailService with UserDB with Database {}
```

Of course this doesn't mean that we should never use inheritance. For example, if we want to make
sure we can talk to various databases, we can make `Database` abstract (not implement at least one
of its methods) and create more specific traits that inherit from `Database`. Note in the code below
that we still use the abstract `Database` as our dependency in `UserDB` and that only when creating
the instance of `EmailService` we have to decide which implementation of `Database` we want to use.

```scala
trait Database {
  def query(/* parameters */): Any
}
trait SQLDatabase extends Database {
  def query(/* parameters */): Any = ???
}
trait MongoDatabase extends Database {
  def query(/* parameters */): Any = ???
}
trait UserDB { this: Database =>
  def getUserData(/* parameters */): Any = ???
}
trait EmailService { this: UserDB =>
  // Can only access UserDb methods, cannot access Database methods
  val userData = getUserData()
}

val emailService1 = new EmailService with UserDB with SQLDatabase {}
val emailService2 = new EmailService with UserDB with MongoDatabase {}
```


Dependency injection in the cake pattern
----------------------------------------

We can use the self-type annotations to declare dependencies between components, such that these
dependencies can be used in the code itself. Because the dependencies are declared on component
level, we can just call the methods declared on the access point instance of the dependent component.

```scala
trait GreeterComponent {

  val greeter: Greeter

  trait Greeter {
    def greet(name: String): String = s"Hello $name!"
  }
}

trait ConversationStarterComponent {
  this: GreeterComponent => // declare a dependency on the GreeterComponent

  val cStarter: ConversationStarter

  trait ConversationStarter {
    // use the Greeter by calling its access point.
    def startConversation(name: String): String = greeter.greet(name) + " How do you do?"
  }
}

object Main extends App with ConversationStarterComponent with GreeterComponent {
  override val greeter: Greeter = new Greeter {}
  override val cStarter: ConversationStarter = new ConversationStarter {}
  
  cStarter.startConversation("Richard")
}
```


Testing
-------

In the test classes, you extend from the '_component under test_' and instantiate a default
implementation of the '_class under test_'. The tests themselves either call this default instance
or create their own local instance of the class.

```scala
class GreeterSpec extends FlatSpec with Matchers with GreeterComponent {

  override val greeter = new Greeter {}
  
  "greet" should "return a greet" in {
    greeter.greet("Bob") shouldBe "Hello Bob!"
  }
}
```

Components that have dependencies can either choose to implement or mock the access point of these 
dependent components.

```scala
class ConversationStarterSpec extends FlatSpec with Matchers with MockFactory with ConversationStarterComponent with GreeterComponent {

  override val greeter = mock[Greeter]
  override val cStarter = new ConversationStarter {}
  
  "startConversation" should "start a conversation with a greet" in {
    val name = "Bob"
    greeter.greet _ expects * once() returning s"Hello $name!"
    
    cStarter.startConversation(name) shouldBe s"Hello $name! How do you do?"
  }
}
```


Further reading/watching
------------------------

* [Scalable Component Abstractions (original paper by Martin Odersky)](http://lampwww.epfl.ch/~odersky/papers/ScalableComponent.pdf)
* [Self-type annotations vs inheritance](http://www.andrewrollins.com/2014/08/07/scala-cake-pattern-self-type-annotations-vs-inheritance/)
* [The Cake Pattern in Practice](https://www.youtube.com/watch?v=DysTHmpDgvk)
* [Scala dependency injection](http://jonasboner.com/real-world-scala-dependency-injection-di/)
* [Cake pattern in depth](http://www.cakesolutions.net/teamblogs/2011/12/19/cake-pattern-in-depth)
* [Screen recording of this workshop (May 2nd, 2017)](https://youtu.be/1fJ34WD0Ed0)
