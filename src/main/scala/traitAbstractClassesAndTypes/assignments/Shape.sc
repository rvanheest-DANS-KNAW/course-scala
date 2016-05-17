import Shape._

abstract class Shape
case class Rectangle(w: Double, h: Double) extends Shape
case class Circle(radius: Double) extends Shape

object Shape {
  def circumference(shape: Shape): Double = {
    shape match {
      case Rectangle(w, h) => 2 * (w + h)
      case Circle(r) => 2 * math.Pi * r
    }
  }

  def area(shape: Shape): Double = {
    shape match {
      case Rectangle(w, h) => w * h
      case Circle(r) => math.Pi * math.pow(r, 2.0)
    }
  }
}

val rectangle = Rectangle(2, 1)
val circle = Circle(1)

circumference(rectangle)
circumference(circle)

area(rectangle)
area(circle)
