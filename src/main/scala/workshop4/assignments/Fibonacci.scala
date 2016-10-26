package workshop4.assignments

import rx.lang.scala.Observable

object Fibonacci extends App {

  def fibonacci: Observable[Int] = {
    Observable.just(0)
      .repeat
      .scan((0, 1)) { case ((pp, p), _) => (p, pp + p) }
      .doOnNext(println)
      .map(_._2)
  }

  fibonacci.take(10).subscribe
}
