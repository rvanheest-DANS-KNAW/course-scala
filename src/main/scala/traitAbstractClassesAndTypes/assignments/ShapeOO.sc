abstract class Shape {
	def circumference: Double
	def area: Double
}
case class Rectangle(w: Double, h: Double) extends Shape {
	def circumference: Double = 2 * (w + h)

	def area: Double = w * h
}
case class Circle(radius: Double) extends Shape {
	def circumference: Double = 2 * math.Pi * radius

	def area: Double = math.Pi * math.pow(radius, 2.0)
}

val rectangle = Rectangle(2, 1)
val circle = Circle(1)

rectangle.circumference
circle.circumference

rectangle.area
circle.area
