package workshop4.assignments

import rx.lang.scala.Observable

object Fibonacci extends App {

  def fibonacci: Observable[Int] = {
    Observable.just(0)
      .repeat
      .scan((0, 1)) { case ((pp, p), _) => (p, pp + p) }
      .map(_._2)
  }

  fibonacci.take(10).subscribe(i => println(i))
//  fibonacci.takeWhile(n => n < 100).subscribe(i => println(i))
//  fibonacci.filter(n => n % 2 == 0).takeWhile(n => n < 1000).subscribe(i => println(i))
}
