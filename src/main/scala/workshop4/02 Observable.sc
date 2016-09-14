import javafx.event.{Event, EventHandler, EventType}
import javafx.scene.Node
import javafx.scene.input.InputEvent

import rx.lang.scala.{Observable, Observer, Subscription}

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
