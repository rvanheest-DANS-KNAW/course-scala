Iterable
========

In the previous workshop we have looked at collections like `List` and `Seq`. We also discussed (higher-order) operators that are defined on these collections: `map`, `flatMap`, `filter`, `fold`, `foreach`, and many others. All collections have a common parent type called `Iterable` and most operators on collections are in some way defined in terms of the methods that define the `Iterable` type.

An `Iterable` is generally defined as an interface (*or trait*) with one method `iterator` that takes no arguments and returns another interface called `Iterator`. This interface in turn has two methods: `hasNext` and `next` which respectively check whether there is a next element and return the next element. 

```scala
trait Iterable[T] {
  def iterator: Iterator[T]
}

trait Iterator[T] {
  def hasNext: Boolean
  def next(): T
}
```

The purpose of the `Iterable` and `Iterator` interfaces is to establish a **pull-based** behavior on collections. Using `hasNext` and `next` you literally pull an element from the collection and process it. Only after you're done with that element, you pull the next element from the collection. This means that you, the consumer of a collection, are in charge as to how fast or how slow the elements are being pulled and processed. You decide when to pull the next element, while the producer of the collection has to obey your commands and just serve the requested element as soon as possible.

In languages like Java and Scala (*and many, many more!*), pull-based collections are so common that syntax has been made to make it even easier to use them. For example, using a general while-loop, you can pull each element from an `Iterable` using the `Iterator` and its `hasNext` and `next` methods. However, using the `Iterable`'s higher-order functions, you can achieve the same with a `foreach`. This function wraps the action to be taken for each element and does the while-loop for you. On the other hand, this iteration process can also be written in a more Java-like style using a for-loop. Notice the similarities between the `foreach` function and the for-loop: both take an element `x` from the collection and specify what action is to be taken with `x`.

```scala
def printElements(itb: Iterable[T]): Unit = {
  val itr = itb.iterator
  while (itr.hasNext) {
    println(itr.next())
  }
}

def printElementsWithForEach[T](itb: Iterable[T]): Unit = {
  itb.foreach(x => println(x))
}

def printElementsWithLoop[T](itb: Iterable[T]): Unit = {
  for (x <- itb) {
    println(x)
  }
}
```

Infinite collections
--------------------
One great feature of `Iterable` is that it does not specify a bound on the size of the collection. While `List` and `Seq` are bounded in the sense that their size must be finite, an `Iterable` can literally have an infinite amount of elements. The only thing you need to do for this is to let `hasNext` always return `true` and make sure `next` can produce a new element every time. In the example below we create an `Iterator` inside an `Iterable` that produces a random number every time `next` is called. Using either a while-loop, for-loop or `foreach` operator you can pull an infinite amount of numbers and process them to your liking.

```scala
def generateInfiniteCollection: Iterable[Double] = {
  new Iterable[Double] {
    def iterator = new Iterator[Double] {
      val generator = Random

      def hasNext = true

      def next() = generator.nextGaussian()
    }
  }
}

generateInfiniteCollection.foreach(println)
```

To get only a finite number of elements from this collection you can just use the `take` operator that is defined on `Iterable`, as we discussed in the previous workshop.

```scala
generateInfiniteCollection.take(5).foreach(println)
```
