import scala.util.Random

def printElements[T](itb: Iterable[T]): Unit = {
  val itr = itb.iterator
  while (itr.hasNext) {
    println(itr.next())
  }
}

def printElementsWithForEach[T](itb: Iterable[T]): Unit = {
  itb.foreach(println)
}

def printElementsWithLoop[T](itb: Iterable[T]): Unit = {
  for (x <- itb) {
    println(x)
  }
}

def generateInfiniteCollection: Iterable[Double] = {
  new Iterable[Double] {
    def iterator = new Iterator[Double] {
      val generator = Random

      def hasNext = true

      def next() = generator.nextGaussian()
    }
  }
}
