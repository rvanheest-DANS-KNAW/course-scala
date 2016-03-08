class Point(val x: Int, val y: Int) {
  def distanceTo(other: Point): Double = {
    val dx = math.abs(x - other.x)
    val dy = math.abs(y - other.y)

    math.sqrt(math.pow(dx, 2) + math.pow(dy, 2))
  }
}
object Point {
  def apply(x: Int, y: Int) = new Point(x, y)
}
object Origin extends Point(0, 0)

Point(3, 4).distanceTo(Origin)

val o1 = Origin
val o2 = Origin
o1.eq(o2)
