import javafx.event.{EventHandler, EventType}
import javafx.scene.Node
import javafx.scene.input.InputEvent

import rx.functions.Action1
import rx.lang.scala.{Observable, Observer, Subscription}
import rx.lang.scala.JavaConverters._

import scala.language.implicitConversions
import scala.util.Random

val emit123: Observable[Int] = Observable.just(1, 2, 3)
val emitFromList: Observable[Int] = Observable.from(List(1, 2, 3, 4))
val emitError: Observable[Int] = Observable.error(new Exception("something went wrong"))
val emitEmpty: Observable[Int] = Observable.empty
val emitNever: Observable[Int] = Observable.never

def observer: Observer[Int] = new Observer[Int] {
  override def onNext(value: Int) = println(s"received onNext event with value: $value")

  override def onError(error: Throwable) = println(s"""received onError event of type ${error.getClass.getSimpleName} and message "${error.getMessage}"""")

  override def onCompleted() = println("received onCompleted event")
}

emit123.subscribe(observer)

emitFromList.subscribe(observer)

emitError.subscribe(observer)

emitEmpty.subscribe(observer)

emitNever.subscribe(observer)

emit123.subscribe(
  value => println(s"received onNext event with value: $value"),
  error => println(s"""received onError event of type ${error.getClass.getSimpleName} and message "${error.getMessage}""""),
  () => println("received onCompleted event")
)

emit123.subscribe(
  value => println(s"received onNext event with value: $value")
)

emitEmpty.subscribe(
  value => println(s"received onNext event with value: $value")
)

def randomNumbers: Observable[Double] = {
  Observable.apply(subscriber => {
    val generator = Random

    while (!subscriber.isUnsubscribed) {
      val number = generator.nextDouble()
      subscriber.onNext(number)
    }
  })
}

randomNumbers.take(5).subscribe(value => println(s"next random number: $value"))

def getEvent[T <: InputEvent](node: Node, event: EventType[T]): Observable[T] = {
  implicit def toHandler(action: T => Unit): EventHandler[T] = {
    new EventHandler[T] { override def handle(e: T): Unit = action(e) }
  }

  Observable.apply[T](subscriber => {
    val handler = (e: T) => subscriber.onNext(e)

    node.addEventHandler(event, handler)
    subscriber.add { node.removeEventHandler(event, handler) }
  })
}

def javaObservableToScalaObservableConverter(): Unit = {

  // given a RxJava Observable (for example from some third party library) ...
  def getJavaObservableFromSomewhere: rx.Observable[Int] ={
    rx.Observable.just(1, 2, 3)
  }

  // ... it is possible to transform this into a RxScala Observable using `asScala`.
  getJavaObservableFromSomewhere
    .asScala
    .subscribe(println, _.printStackTrace(), () => println("done"))
}

def scalaObservableToJavaObservableConverter(): Unit = {

  // given a function that takes a RxJava Observable as its input and returns a RxJava Subscription ...
  def useJavaObservableSomewhere(obs: rx.Observable[_ <: Int]): rx.Subscription = {
    obs.subscribe(new Action1[Int] { override def call(i: Int): Unit = println(i) }) // RxJava requires an Action1 object here rather than a lambda expression.
  }

  val scalaObservable: Observable[Int] = Observable.just(1, 2, 3)

  // ... you can give a RxScala Observable as input and call `asJava`on it ...
  val javaSubscription: rx.Subscription = useJavaObservableSomewhere(scalaObservable.asJava)

   // ... after which you get a RxJava Subscription back, which can be used again in a RxScala setting using `asScalaSubscription` ...
  val scalaSubscription: Subscription = javaSubscription.asScalaSubscription

  // ... and convert it back to a RxJava Subscription using `asJavaSubscription`.
  val javaSubscriptionAgain: rx.Subscription = scalaSubscription.asJavaSubscription
}
