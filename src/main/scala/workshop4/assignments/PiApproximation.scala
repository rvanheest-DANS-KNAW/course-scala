package workshop4.assignments

import rx.lang.scala.Observable

import scala.language.implicitConversions
import scala.util.Random

// see https://en.wikipedia.org/wiki/Approximations_of_%CF%80#Summing_a_circle.27s_area
// TODO make custom text instead of refering to Wikipedia
object PiApproximation {

  /**
   * Convert a `Boolean` to an `Int`. If `true` then 1 else 0.
   *
   * @param b the boolean to be converted
   * @return the integer conversion
   */
  def booleanToInt(b: Boolean): Int = if (b) 1 else 0

  /**
   * Groups the number of successful hits with the total number of hits
   *
   * @param successes number of successful hits
   * @param total total number of hits
   */
  case class Hits(successes: Int, total: Int)
  object Hits {
    def empty: Hits = Hits(0, 0)
  }

  def random: Observable[Double] = {
    Observable.defer(Observable.just(Random.nextDouble())).repeat
  }

  def main(args: Array[String]): Unit = {

    val groupsOfTwo: Observable[Seq[Double]] = ???

    val insideCircle: Observable[Int] = ???

    val hits: Observable[Hits] = ???

    val piApprox: Observable[Double] = ???

    val oneMillionApproximations: Observable[Double] = ???

    oneMillionApproximations.subscribe(println(_), _.printStackTrace(), () => println("done"))
  }
}
