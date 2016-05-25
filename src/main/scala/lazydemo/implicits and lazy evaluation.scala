package temp

object Main extends App {

  // call-by-name vs. call-by-value
  implicit class BooleanExtensions(val b: Boolean) extends AnyVal {

    def and(b2: => Boolean) = {
      if (b) b2
      else false
    }

    def or(b2: => Boolean) = {
      if (b) true
      else b2
    }
  }

  def loop: Boolean = loop

  val bool1 = false
  val bool2 = true

  println(bool1 and bool1)
  println(bool1 and bool2)
  println(bool2 and bool1)
  println(bool2 and bool2)
  println(bool1 and loop)
}
