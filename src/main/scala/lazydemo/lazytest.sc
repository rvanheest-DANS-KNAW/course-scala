class Foo {
  val x = {
    println("x evaluated")
    5 + 2
  }

  lazy val y = {
    println("y evaluated")
    3 + 4
  }

  def z = {
    println("z evaluated")
    1 + 6
  }
}

// creating a class
// this will automatically evaluate x and hence print "x evaluated"
val f = new Foo

// when f.x, it will NOT evaluate x again
f.x

// when f.y for the first time, it will evaluate y and hence print "y evaluated"
f.y

// when f.z, it will evaluate z and hence print "z evaluated"
f.z

// when f.y again, it will NOT evaluate y again
f.y

// when f.z again, it will evalute z again and hence print "z evaluated" again
f.z
