Operators on Observable
=======================

As we have discussed before, an `Observable` is a collection just like an `Iterable`. Their main difference, however, is that in an `Observable` data is **pushed** at you, while you **pull** data from an `Iterable`. Otherwise the two are exactly the same! Therefore it is possible to have the same kinds of operators as we have seen with `Iterable` collections in [workshop 3]. We can have operators like `map`, `filter`, `take`, `drop`, `foldLeft`, etc.

[workshop 3]: ../workshop3/README.md

This section will introduce some basic operators on `Observable` and go over there behavior one by one. As there are 300+ operators in total (and still increasing!) we are not able to discuss them all. However, as you get more experience with RxJava/RxScala, you will find that most operators are hardly ever used and that only a small subset is needed to get started.
 
The main source of wisdom on the behavior of each operator comes from the [Observable javadoc] page. Here each operator is described in great detail, including diagrams which we will discuss next. For RxScala similar information can be found on the [Observable scaladoc] page. See also the [RxScala comparison page] to compare naming conventions between RxJava and RxScala.

[Observable javadoc]: http://reactivex.io/RxJava/javadoc/rx/Observable.html
[Observable scaladoc]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable
[RxScala comparison page]: http://reactivex.io/rxscala/comparison.html


Marble diagrams
---------------

The [Observable javadoc] makes use so called marble diagrams. These show the behavior of a certain operator in a fairly simple and understandable way. For example, the diagram below shows an imaginairy operator `flip`, which turns the marbles upside-down as they are emitted by the source `Observable`. It is important to learn how to read these kinds of diagrams, as this forms the best part of the documentation.

![flip](https://raw.github.com/wiki/ReactiveX/RxJava/images/rx-operators/legend.png)

Some operators also have interactive marble diagrams, such that you can move the marbles and see the exact behavior of the operator. You can find this at [rxmarbles.com].

[rxmarbles.com]: http://rxmarbles.com/


`filter`
--------

The first operator to look at is [`filter`]. Just as in `Iterable` it takes a function (predicate) of type `T => Boolean` and discards all elements that do not satisfy the predicate.

[`filter`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@filter(predicate:T=>Boolean):rx.lang.scala.Observable[T]

![filter](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/filter.png)

For example, the `Observable` in the following code contains a sequence of numbers. The `filter` operator only lets even numbers pass and discards all odd numbers.

```scala
Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .filter(i => i % 2 == 0)
```

**Don't forget:** this code snippet returns an `Observable[Int]` and does not do anything! It only starts working once you subscribe to it, for example using `.subscribe(i => println(i))`.

```scala
Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .filter(i => i % 2 == 0)
  .subscribe(i => println(i))
// prints 0, 2, 4, 6, 8
```


`map`
-----

Another well known operator from the previous workshop is `map`. It also has a single argument, which is a function of type `T => R`. It takes each element in the stream, applies the function on it and sends the result down the stream. Note that no elements are discarded here!

[`map`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@map[R](func:T=>R):rx.lang.scala.Observable[R]

![map](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/map.png)

The code below shows two simple use case for `map`. In the first example we transform every `Int` to a `String`, whereas in the second example we increment every `Int`.

```scala
Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(i => s"'$i'")
  .subscribe(s => println(s))
// prints '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(i => i + 1)
  .subscribe(i => println(i))
// prints 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
```


Chaining operations
-------------------

Rather than just having one operator for an `Observable`, we can chain operators into long sequences. The example below shows an `Observable[String]` where some elements are integer and other elements are not. Using the `filter` operator we only keep those `String`s for which *all* characters are digits. After that we can safely `map` those to integers.

We can do this operator chaining with all operators defined on `Observable` and thereby define the complex behavior that is required in our software.

```scala
Observable.just("81", "42", "twee", "150", "15een")
  .filter(s => s.forall(c => c.isDigit))
  .map(s => s.toInt)
  .subscribe(i => println(i))
// prints 81, 42, 150
```
