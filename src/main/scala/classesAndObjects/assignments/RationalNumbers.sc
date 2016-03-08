import scala.annotation.tailrec

class Rational(n: Int, d: Int = 1) {

  require(d != 0)
  private val g = gcd(n.abs, d.abs)
  val numer = n / g
  val denom = d / g

  def +(that: Rational): Rational = {
    new Rational(numer * that.denom + that.numer * denom, denom * that.denom)
  }

  def +(i: Int): Rational = {
    new Rational(numer + i * denom, denom)
  }

  def -(that: Rational): Rational = {
    new Rational(numer * that.denom - that.numer * denom, denom * that.denom)
  }

  def -(i: Int): Rational = {
    new Rational(numer - i * denom, denom)
  }

  def *(that: Rational): Rational = {
    new Rational(numer * that.numer, denom * that.denom)
  }

  def *(i: Int): Rational = {
    new Rational(numer * i, denom)
  }

  def /(that: Rational): Rational = {
    new Rational(numer * that.denom, denom * that.numer)
  }

  def /(i: Int): Rational = {
    new Rational(numer, denom * i)
  }

  override def equals(other: Any): Boolean = other match {
    case that: Rational => numer == that.numer && denom == that.denom
    case _ => false
  }

  override def toString = {
    if (denom == 1) numer.toString
    else numer + "/" + denom
  }

  @tailrec
  private def gcd(a: Int, b: Int): Int = {
    if (b == 0) a
    else gcd(b, a % b)
  }
}

val r1 = new Rational(2, 4)
val r2 = new Rational(1, 2)
val r3 = new Rational(3, 4)

r1 == r2
r2 == r3

r1 + r2
r2 + r3
r2 + (r3 * 2)
