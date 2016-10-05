package workshop4.assignments

import rx.lang.scala.Observable

object DiscardThirdElement extends App {

  def discardThirdBuffer[T](obs: Observable[T]): Observable[T] = ???

  def discardThirdWindow[T](obs: Observable[T]): Observable[T] = ???

  println("discard every third element using buffer")
  discardThirdBuffer(Observable.from(1 to 20)).subscribe(println(_), e => e.printStackTrace(), () => println("done"))

  println("discard every third element using window")
  discardThirdWindow(Observable.from(1 to 20)).subscribe(println(_), e => e.printStackTrace(), () => println("done"))
}
