package workshop4.assignments.solutions

import rx.lang.scala.Observable

import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.language.postfixOps

trait DiscardThirdElementSolution {

  // comment and uncomment the choices below to see the differences

  def discardThirdBuffer[T](obs: Observable[T]): Observable[T] = {
    obs.tumblingBuffer(3)
      .flatMapIterable(_.take(2))
//      .flatMapIterable(_.dropRight(1))
  }

  def discardThirdWindow[T](obs: Observable[T]): Observable[T] = {
    obs.tumbling(3)
      .flatMap(_.take(2))
//      .flatMap(_.dropRight(1))
  }
}

object DiscardThirdElementWithBuffer extends App with DiscardThirdElementSolution {

  println("discard every third element using buffer")

  discardThirdBuffer(Observable.from(1 to 20))
    .subscribe(println(_), e => e.printStackTrace(), () => println("done"))
}

object DiscardThirdElementWithBufferAndInterval extends App with DiscardThirdElementSolution {

  println("discard every third element using buffer")

  discardThirdBuffer(Observable.interval(1 second).take(20))
    .subscribe(println(_), e => e.printStackTrace(), () => { println("done"); System.exit(0) })

  StdIn.readLine()
}

object DiscardThirdElementWithWindow extends App with DiscardThirdElementSolution {

  println("discard every third element using window")

  discardThirdWindow(Observable.from(1 to 20))
    .subscribe(println(_), e => e.printStackTrace(), () => println("done"))
}

object DiscardThirdElementWithWindowAndInterval extends App with DiscardThirdElementSolution {

  println("discard every third element using window")

  discardThirdWindow(Observable.interval(1 second).take(20))
    .subscribe(println(_), e => e.printStackTrace(), () => { println("done"); System.exit(0) })

  StdIn.readLine()
}
