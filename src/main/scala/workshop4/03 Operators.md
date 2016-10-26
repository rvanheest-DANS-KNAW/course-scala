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

The [Observable javadoc] makes use so called marble diagrams. These show the behavior of a certain operator in a fairly simple and understandable way. For example, the diagram below shows an imaginary operator `flip`, which turns the marbles upside-down as they are emitted by the source `Observable`. It is important to learn how to read these kinds of diagrams, as this forms the best part of the documentation.

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
// prints: 0, 2, 4, 6, 8
```


`map`
-----

Another well known operator from the previous workshop is `map`. It also has a single argument, which is a function of type `T => R`. It takes each element in the stream, applies the function on it and sends the result down the stream. Note that no elements are discarded here!

[`map`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@map[R](func:T=>R):rx.lang.scala.Observable[R]

![map](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/map.png)

The code below shows two simple use case for `map`. In the first example we transform every `Int` to a `String`, whereas in the second example we increment every `Int`.

```scala
Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(i => i.toString)
  .subscribe(s => println(s))
// prints: "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(i => i + 1)
  .subscribe(i => println(i))
// prints: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
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
// prints: 81, 42, 150
```


`foldLeft`
----------

A third operator from the previous workshop is [`foldLeft`]. Given a collection of data, it calculated some form of accumulation of the data, such as the sum or product of numbers or the concatenation of `String`s. This is also the case for the `Observable`: all events get accumulated and after the stream is *completed*, the resulting accumulation is emitted downstream.

[`foldLeft`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@foldLeft[R](initialValue:R)(accumulator:(R,T)=>R):rx.lang.scala.Observable[R]

![foldLeft](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/reduceSeed.png)

In the example below we accumulate all numbers in the stream. Again, notice that (as shown in the image above) the result is only emitted when the original stream completes!

```scala
Observable.just(0, 1, 2, 3, 4)
  .foldLeft(0)((acc, i) => acc + i)
  .subscribe(i => println(i))
// prints: 10
```


`scan`
------

Although `foldLeft` might be useful in some cases of streams, it is also quite risky to use this operator! If an `Observable` produces infinitely many elements or never completes, the `foldLeft` will not emit any values. An alternative operator that is most often used as an accumulator is [`scan`]. This operator is similar to `foldLeft` in that it accumulates the values of its `Observable`, but different in that it emits all intermediate results as well. In other words, it does not wait for an `onCompleted` event to emit the accumulated value, but rather accumulates the value and emit this value immediately.

[`scan`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@scan[R](initialValue:R)(accumulator:(R,T)=>R):rx.lang.scala.Observable[R])

![scan](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/scanSeed.png)

The example below is the same as the one from the `foldLeft` section, but with the `scan` operator. Notice the difference in its results! Also note that the resulting stream **always** starts with the `seed` value (in this case `0`) and only emits the accumulations after that! If you don't want the seed value (which is often the case!), you can use a `drop(1)` operator (which we will discuss below) right after the `scan`.

```scala
Observable.just(0, 1, 2, 3, 4)
  .scan(0)((acc, i) => acc + i)
  .subscribe(i => println(i))
// prints: 0, 0, 1, 3, 6, 10
```


`distinct` and `distinctUntilChanged`
-------------------------------------

In some cases you only want to have a stream with unique elements. The first time an event occurs, you want to handle it, but after that you are not interested in the same kind of event anymore. In other cases you might not be interested in the same event occurring multiple times in a row but are interested in them if they occur after other events have occurred.

For these cases you can use the `distinct` and `distinctUntilChanged` operators. These operators come in a couple of flavours, which are depicted in the marble diagrams below.

Regarding `distinct`, we can [match on the elements in the `Observable` themselves] or on some [property of the elements]. For example:
 
```scala
Observable.just(1, 2, 3, 2, 4, 1, 5)
  .distinct
  .subscribe(i => println(i))
// prints: 1, 2, 3, 4, 5

Observable.just("a", "abc", "ab", "aaa", "b")
  .distinct(s => s.length)
  .subscribe(s => println(s))
// prints: "a", "abc", "ab"
```

[match on the elements in the `Observable` themselves]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@distinct:rx.lang.scala.Observable[T]
[property of the elements]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@distinct[U](keySelector:T=>U):rx.lang.scala.Observable[T]

![distinct](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/distinct.png)

---

![distinct-with-selector](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/distinct.key.png)

Regarding `distinctUntilChanged` we have the same kinds of operators, [with] and [without] a selector:

```scala
Observable.just(1, 2, 2, 3, 2, 2, 2)
  .distinctUntilChanged
  .subscribe(i => println(i))

Observable.just("a", "b", "ab", "abc", "bc")
  .distinctUntilChanged(s => s.length)
  .subscribe(s => println(s))
```

[with]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@distinctUntilChanged[U](keySelector:T=>U):rx.lang.scala.Observable[T]
[without]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@distinctUntilChanged:rx.lang.scala.Observable[T]

![distinctUntilChanged](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/distinctUntilChanged.png)

---

![distinctUntilChanged-with-selector](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/distinctUntilChanged.key.png)

As a final example, `distinct` is used in the [`jobMonitor`] in [`easy-ingest-dispatcher`]. Here every couple of seconds a list of all files and folders in the `depositsDirectory` is asked from the filesystem. However, we are only interested in the ones that were just added. Given the `Observable[File]` we gained before, we can then do a `distinct(_.getName)` where we check the distinctness of the `File`'s name.

[`jobMonitor`]: https://github.com/rvanheest-DANS-KNAW/easy-ingest-dispatcher/blob/7f802ad62fa78a74b628a43f58f050599c2ce59d/src/main/scala/nl/knaw/dans/easy/ingest_dispatcher/EasyIngestDispatcher.scala#L80
[`easy-ingest-dispatcher`]: https://github.com/DANS-KNAW/easy-ingest-dispatcher


`drop`/`skip`
-------------

Another way get rid of certain elements is to `drop` a certain number of elements, in this case the first elements of a stream. You can specify [a certain number of elements] to be dropped or specify [a timeframe] in which all elements are dropped.

**Note 1:** `drop` always acts on the first elements of a stream!
**Note 2:** In RxJava this operator is called [`skip`]. RxScala choose to use `drop` instead because of naming conventions in the Scala Collections API.
**Note 3:** Because the latter of the two flavours of `drop` works on the notion of time, we will ignore this specific operator for now and come back to it in the next workshop.

[a certain number of elements]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@drop(n:Int):rx.lang.scala.Observable[T]
[a timeframe]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@drop(time:scala.concurrent.duration.Duration):rx.lang.scala.Observable[T]
[`skip`]: http://reactivex.io/RxJava/javadoc/rx/Observable.html#skip(int)

![drop-number](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/skip.png)

```scala
Observable.just(1, 2, 3, 4, 5)
  .drop(3)
  .subscribe(i => println(i))
// prints: 4, 5
```

An alternative to `drop(n)` is [`dropWhile`], which discards all the elements of the `Observable` as long as its predicate remains `true`.

**Note:** just as with `drop`, this is called [`skipWhile`] in RxJava.

[`dropWhile`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@dropWhile(predicate:T=>Boolean):rx.lang.scala.Observable[T]
[`skipWhile`]: http://reactivex.io/RxJava/javadoc/rx/Observable.html#skipWhile(rx.functions.Func1)

![dropWhile](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/skipWhile.png)

```scala
Observable.just(1, 2, 3, 4, 5)
  .dropWhile(i => i < 3)
  .subscribe(i => println(i))
// prints: 3, 4, 5
```


`take`
------

Similar to `drop`, there is a [`take(n)`] operator which only keeps the first `n` elements of the stream and drops all elements after that. Notice in the diagram below that, because after `n` elements no more elements will follow, the `Observable` completes earlier than usual, that is: immediately after the `n`th element is emitted.

[`take(n)`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@take(n:Int):rx.lang.scala.Observable[T]

![take](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/take.png)

```scala
Observable.just(0, 1, 2, 3, 4)
  .take(3)
  .subscribe(i => println(i))
// prints: 0, 1, 2
```

Similar to a `dropWhile` there is a [`takeWhile`] that takes a predicate and returns a stream with all elements of the original stream as long as the predicate stays `true`. Once the predicate becomes `false` on an element, the stream completes and no more elements are evaluated.

[`takeWhile`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@takeWhile(predicate:T=>Boolean):rx.lang.scala.Observable[T]

![takeWhile](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/takeWhile.png)


Side effects
------------

So far we have seen a couple of operators that transform, filter or limit a stream of events/data. These operators are supposed to run in a *pure* context, meaning that they are not allowed to do side effects such as writing to standard output, a file or a database, mutating state, or putting a thread to sleep. This is all part of the functional style that is incorporated in the Rx libraries.

To perform side effects, we have special methods to our disposal to do so. These are usually identified by the name of the event on which's occurence the side effect needs to take place, prefixed with `do`:

* [doOnNext](http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnNext(onNext:T=>Unit):rx.lang.scala.Observable[T]) - takes a function of type `T => Unit` that is applied with every element of the stream, after which the original element is passed on downstream.
* [doOnError](http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnError(onError:Throwable=>Unit):rx.lang.scala.Observable[T]) - takes a function of type `Throwable => Unit` and applies it to the `onError` event that might terminate the stream. Note that it does not consume the error, but only applies it to the given function and propagate it to downstream when its finished.
* [doOnCompleted](http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnCompleted(onCompleted:=>Unit):rx.lang.scala.Observable[T]) - executes the code block given as argument whenever the stream encounters an `onCompleted` event. Note that it does not consume this event!
* [doOnSubscribe](http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnSubscribe(onSubscribe:=>Unit):rx.lang.scala.Observable[T]) - executes the code block given as argument whenever the stream is subscribed to by any `Observer`.
* [doOnUnsubscribe](http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnUnsubscribe(onUnsubscribe:=>Unit):rx.lang.scala.Observable[T]) - executes the code block given as argument whenever the stream is unsubcribed from.
* [doOnTerminate]() - executes the code block given as argument whenever the stream encounters either an `onCompleted` or `onError` event.
* [doAfterTerminate]() - executes the code block given as argument after the stream is unsubscribed from.

In general these operators are fairly useful for debugging an operator sequence, to see what elements are going through it or what is going on inside.

```scala
Observable.just(1, 2, 3, 4)
  .doOnNext(i => println(s"onNext: $i"))
  .doOnError(e => println(s"onError: ${e.getMessage}"))
  .doOnCompleted { println("completed") }
  .doOnSubscribe { println("subscribed") }
  .doOnUnsubscribe { println("unsubscribed") }
  .doOnTerminate { println("on terminate") }
  .doAfterTerminate { println("after terminate") }
  .subscribe

// prints:
/*
  subscribed
  onNext: 1
  onNext: 2
  onNext: 3
  onNext: 4
  completed
  on terminate
  unsubscribed
  after terminate
 */
```

---

```scala
Observable.error(new Exception("ERROR!!!"))
  .doOnNext(i => println(s"onNext: $i"))
  .doOnError(e => println(s"onError: ${e.getMessage}"))
  .doOnCompleted { println("completed") }
  .doOnSubscribe { println("subscribed") }
  .doOnUnsubscribe { println("unsubscribed") }
  .doOnTerminate { println("on terminate") }
  .doAfterTerminate { println("after terminate") }
  .subscribe(_ => {}, e => println(s"the error was $e"))

// prints:
/*
  subscribed
  onError: ERROR!!!
  on terminate
  the error was java.lang.Exception: ERROR!!!
  unsubscribed
  after terminate
 */
```


Error handling
--------------

As mentioned before, `onError` is a terminal event, meaning that once this event happens, no other events like `onNext`, `onError` or `onCompleted` can ever occur in this stream. However, in production level systems it is not expected that a service terminates itself whenever an error occurs; especially when it is not a fatal error. Preferably the error is logged, after which the stream recovers itself and carries on.

This is exactly what the [`retry`] operators can do. When included in the operator sequence, they consume the error, do not propagate it and resubscribe to the upstream `Observable`. If no parameter is given to `retry`, it will retry forever, without ever propagating an error. When [specified as an argument], the `retry` will only happen a finite amount of times, after which the latest error is still propagated.

[`retry`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@retry:rx.lang.scala.Observable[T]
[specified as an argument]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@retry(retryCount:Long):rx.lang.scala.Observable[T]

![retry](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/retry.png)

In [`easy-ingest-dispatcher`] the `retry` is included in the [`run` method] to ensure that the `jobMonitor` always keeps running, even if an error occurs. Since `retry` consumes the exception, it is a good practice to have a `doOnError` right before it that logs the exception.

[`run` method]: https://github.com/rvanheest-DANS-KNAW/easy-ingest-dispatcher/blob/7f802ad62fa78a74b628a43f58f050599c2ce59d/src/main/scala/nl/knaw/dans/easy/ingest_dispatcher/EasyIngestDispatcher.scala#L63

Another way of dealing with an `onError` event is by using [`onErrorResumeNext`]. This operator takes a function of type `Throwable => Observable[T]`, where the `Throwable` is the exception encountered in the `onError` event and `Observable[T]` is the stream with which to continue the operator sequence.

[`onErrorResumeNext`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@onErrorResumeNext[U>:T](resumeFunction:Throwable=>rx.lang.scala.Observable[U]):rx.lang.scala.Observable[U]

![onErrorResumeNext](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/onErrorResumeNext.png)

Again it is often useful to first log the error using `doOnError` before consuming it in `doOnErrorResumeNext`.

```scala
Observable.error(new Exception("useful error message"))
  .doOnError(e => println(s"onError: ${e.getMessage}"))
  .onErrorResumeNext(e => Observable.just(-1))
  .subscribe(i => println(i))
```


An example
----------

There are many more operators that could be discussed in this section. The operators listed above are the ones that are most notably used in the current code of [EASY]. In this final section on operators we will look at a real life example of using an `Observable`. We will create a stream of numbers that form the [Fibonacci sequence]. Starting of with the values `{0, 1}`, this sequence accumulates the previous two values as its next value: `fib(n) = fib(n - 1) + fib(n - 2)`. So, this first elements of this (infinite) sequence are: `{1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, ...}`

To write this in terms of `Observable`, we start with an `Observable.just(0).repeat` that will emit an infinite stream of elements (in this case they are all `0`). We will not actually use the value itself, but rather use the fact that an element is emitted (a.k.a. an `onNext` occured). 
  
The accumulation phase is done using a `scan` operation: `.scan((0, 1)) { case ((pp, p), _) => (p, pp + p) }`. Here we use a *tuple* as the seed value: `(0, 1)`. To get the next element of the sequence, we calculate the sum of these values (`pp + p`) and tuple that with the latest value (`p`). Note that we discard the original elements (which were all `0`s) by using an `_`. The resulting tuple will then become the next seed value *and* be emitted downstream for further use.

The resulting stream therefore has type `Observable[(Int, Int)]`, where each tuple contains the *previous* and *current* number in the sequence. Since we're only interested in the *current* value, we have to `map` the stream and get out the right-hand side of the tuple: `.map(_._2)`.

In the full code example below we define the function `fibonacci` to be this infinite stream of values. It is good practice to leave out any bounds on the number of elements (a.k.a. how many of the elements in the sequence do you want) in this function and let the user decide how many he wants.

Below is a table of the values that are emitted by each of the operators in this function. This makes it especially clear what the `scan` operator is doing.

| `Observable.just(0).repeat` | `scan`   | `map` |
| --------------------------- | -------- | ----- |
|                             | (0, 1)   | 1     |
| 0                           | (1, 1)   | 1     |
| 0                           | (1, 2)   | 2     |
| 0                           | (2, 3)   | 3     |
| 0                           | (3, 5)   | 5     |
| 0                           | (5, 8)   | 8     |
| 0                           | (8, 13)  | 13    |
| 0                           | (13, 21) | 21    |
| 0                           | (21, 34) | 34    |
| 0                           | (34, 55) | 55    |

[EASY]: https://easy.dans.knaw.nl/ui/home
[Fibonacci sequence]: https://en.wikipedia.org/wiki/Fibonacci_number

```scala
def fibonacci: Observable[Int] = {
  Observable.just(0)
    .repeat
    .scan((0, 1)) { case ((pp, p), _) => (p, pp + p) }
    .map(_._2)
}

fibonacci.take(10).subscribe(i => println(i))
```

This code can also be found [here], for you to play with.

[here]: assignments/Fibonacci.scala
