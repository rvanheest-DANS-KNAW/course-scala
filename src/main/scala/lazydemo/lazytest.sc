class Foo {
  val x = {
    println("foo x")
    5 + 2
  }

  lazy val y = {
    println("foo y")
    3 + 4
  }

  def z = {
    println("foo z")
    1 + 6
  }
}

val f = new Foo
f.x
f.y

f.z
f.y
f.z
