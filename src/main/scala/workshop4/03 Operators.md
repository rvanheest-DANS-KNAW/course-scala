Operators on Observable
=======================

As we have discussed before, an `Observable` is a collection just like an `Iterable`. They are different in that, 
in an `Observable`, data is **pushed** *to* you, while you **pull** data *from* an `Iterable`. Otherwise the two are
exactly the same! Therefore it is possible to have the same kinds of operators as we have seen with `Iterable` collections
in [workshop 3]. We can have operators like `map`, `filter`, `take`, `drop`, `foldLeft`, etc.

[workshop 3]: ../workshop3/README.md

This section will introduce some basic operators on `Observable` and go over their behaviors one by one. As there are
300+ operators in total (and still increasing!), we are not able to discuss them all. However, as you get more experience
with RxJava/RxScala, you will find that most operators are hardly ever used and that only a small subset is needed to
accomplish most tasks.
 
The main source of wisdom on the behavior of each operator is the [Observable javadoc] page. Here each operator
is described in great detail, including a special kind of diagram, which we will discuss next. For RxScala similar 
information can be found on the [Observable scaladoc] page. See also the [RxScala comparison page] to compare naming 
conventions between RxJava and RxScala.

[Observable javadoc]: http://reactivex.io/RxJava/javadoc/rx/Observable.html
[Observable scaladoc]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable
[RxScala comparison page]: http://reactivex.io/rxscala/comparison.html


Marble diagrams
---------------

The [Observable javadoc] makes use of so called marble diagrams. These show the behavior of a certain operator in a fairly
simple and understandable way. For example, the diagram below shows an imaginary operator `flip`, which turns the marbles
upside-down as they are emitted by the source `Observable`. It is important to learn how to read these kinds of diagrams,
as this forms the best part of the documentation.

![flip](https://raw.github.com/wiki/ReactiveX/RxJava/images/rx-operators/legend.png)

Some operators also have interactive marble diagrams, which let you move the marbles and examine the exact behavior of
the operator. You can find these at [rxmarbles.com].

[rxmarbles.com]: http://rxmarbles.com/


`filter`
--------

The first operator to look at is [`filter`]. Just as in `Iterable` it takes a function (predicate) of type `T => Boolean`
and discards all elements that do not satisfy the predicate.

[`filter`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@filter(predicate:T=>Boolean):rx.lang.scala.Observable[T]

![filter](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/filter.png)

For example, the `Observable` in the following code contains a sequence of numbers. The `filter` operator only lets even
numbers pass and discards all odd numbers.

```scala
Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .filter(_ % 2 == 0)
```

**Don't forget:** this code snippet returns an `Observable[Int]` and does not do anything! It only starts working once you
subscribe to it, for example using `.subscribe(i => println(i))`, or, shorter: `.subscribe(println(_))`

```scala
Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .filter(_ % 2 == 0)
  .subscribe(println(_))
// prints: 0, 2, 4, 6, 8
```


`map`
-----

Another well-known operator from the previous workshop is `map`. It also has a single argument, which is a function of
type `T => R`. It takes each element in the stream, applies the function on it and sends the result down the stream.
Note that no elements are discarded here!

[`map`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@map[R](func:T=>R):rx.lang.scala.Observable[R]

![map](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/map.png)

The code below shows two simple use cases for `map`. In the first example we transform every `Int` to a `String` and
in the second example we increment every `Int`.

```scala
Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(_.toString)
  .subscribe(println(_))
// prints: "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(_ + 1)
  .subscribe(println(_))
// prints: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
```


Chaining operations
-------------------

Rather than just having one operator for an `Observable`, we can chain operators into long sequences. The example below
shows an `Observable[String]` where some elements are integers and other elements are not. Using the `filter` operator we
only keep those `String`s for which *all* characters are digits. After that we can safely `map` those to integers.

We can do this operator chaining with all operators defined on `Observable` and thereby define the complex behavior that
is required in our software.

```scala
Observable.just("81", "42", "twee", "150", "15een")
  .filter(_.forall(_.isDigit))
  .map(_.toInt)
  .subscribe(println(_))
// prints: 81, 42, 150
```


`foldLeft`
----------

A third operator from the previous workshop is [`foldLeft`]. Given a collection of data, it calculates some form of
accumulation of the data, such as the sum or product of numbers or the concatenation of `String`s. This is also the
case for the `Observable`: all events get accumulated and after the stream is *completed*, the resulting accumulation
is emitted downstream.

[`foldLeft`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@foldLeft[R](initialValue:R\)\(accumulator:\(R,T)=>R\):rx.lang.scala.Observable[R]

![foldLeft](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/reduceSeed.png)

In the example below we accumulate all numbers in the stream. Again, notice that (as shown in the image above) the
result is only emitted when the original stream completes!

```scala
Observable.just(0, 1, 2, 3, 4)
  .foldLeft(0)((acc, i) => acc + i)
  .subscribe(println(_))
// prints: 10
```


`scan`
------

Although `foldLeft` might be useful in some cases of streams, it is also quite risky to use this operator! If an
`Observable` produces infinitely many elements or never completes, the `foldLeft` will not emit any values. An
alternative operator that is most often used as an accumulator is [`scan`]. This operator is similar to `foldLeft`
in that it accumulates the values of its `Observable`, but different in that it emits all intermediate results as well.
In other words, it does not wait for an `onCompleted` event to emit the accumulated value, but rather accumulates the
value and emit this value immediately.

[`scan`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@scan[R](initialValue:R\)\(accumulator:\(R,T)=>R\):rx.lang.scala.Observable[R]

![scan](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/scanSeed.png)

The example below is the same as the one from the `foldLeft` section, but with the `scan` operator. Notice the difference
in its results! Also note that the resulting stream **always** starts with the `seed` value (in this case `0`) and only
emits the accumulations after that! If you don't want the seed value (which is often the case!), you can use a `drop(1)`
operator (which we will discuss below) right after the `scan`.

```scala
Observable.just(0, 1, 2, 3, 4)
  .scan(0)((acc, i) => acc + i)
  .subscribe(println(_))
// prints: 0, 0, 1, 3, 6, 10
```


`distinct` and `distinctUntilChanged`
-------------------------------------

In some cases you only want to have a stream with unique elements. The first time an event occurs, you want to handle it,
but after that you are not interested in the same kind of event anymore. In other cases you might not be interested in the
same event occurring multiple times in a row but are interested in them if they occur after other events have occurred.

For these cases you can use the `distinct` and `distinctUntilChanged` operators. These operators come in a couple of flavours,
which are depicted in the marble diagrams below.

Regarding `distinct`, we can [match on the elements in the `Observable` themselves] or on some [property of the elements].
For example:
 
```scala
Observable.just(1, 2, 3, 2, 4, 1, 5)
  .distinct
  .subscribe(println(_))
// prints: 1, 2, 3, 4, 5

Observable.just("a", "abc", "ab", "aaa", "b")
  .distinct(_.length)
  .subscribe(println(_))
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
  .subscribe(println(_))

Observable.just("a", "b", "ab", "abc", "bc")
  .distinctUntilChanged(s => s.length)
  .subscribe(println(_))
```

[with]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@distinctUntilChanged[U](keySelector:T=>U):rx.lang.scala.Observable[T]
[without]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@distinctUntilChanged:rx.lang.scala.Observable[T]

![distinctUntilChanged](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/distinctUntilChanged.png)

---

![distinctUntilChanged-with-selector](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/distinctUntilChanged.key.png)

As a final example, `distinct` is used in the [`jobMonitor`] in [`easy-ingest-dispatcher`]. Here every couple of seconds
the list of all files and folders in the `depositsDirectory` is retrieved from the filesystem. However, we are only interested
in the ones that were just added. Given the `Observable[File]` we gained before, we can then do a `distinct(_.getName)`
where we check the distinctness of the `File`'s name.

[`jobMonitor`]: https://github.com/rvanheest-DANS-KNAW/easy-ingest-dispatcher/blob/7f802ad62fa78a74b628a43f58f050599c2ce59d/src/main/scala/nl/knaw/dans/easy/ingest_dispatcher/EasyIngestDispatcher.scala#L80
[`easy-ingest-dispatcher`]: https://github.com/DANS-KNAW/easy-ingest-dispatcher


`drop`/`skip`
-------------

Another way to get rid of certain elements is to `drop` a certain number of elements, in this case the first elements of a
stream. You can specify [a certain number of elements] to be dropped or specify [a timeframe] in which all elements are dropped.

**Note 1:** `drop` always acts on the first elements of a stream!

**Note 2:** In RxJava this operator is called [`skip`]. RxScala choose to use `drop` instead because of naming conventions
in the Scala Collections API.

**Note 3:** Because the latter of the two flavours of `drop` works on the notion of time, we will ignore this specific operator
for now and come back to it in the next workshop.

[a certain number of elements]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@drop(n:Int):rx.lang.scala.Observable[T]
[a timeframe]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@drop(time:scala.concurrent.duration.Duration):rx.lang.scala.Observable[T]
[`skip`]: http://reactivex.io/RxJava/javadoc/rx/Observable.html#skip(int)

![drop-number](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/skip.png)

```scala
Observable.just(1, 2, 3, 4, 5)
  .drop(3)
  .subscribe(println(_))
// prints: 4, 5
```

An alternative to `drop(n)` is [`dropWhile`], which discards all the elements of the `Observable` as long as its predicate
remains `true`.

**Note:** just as with `drop`, this is called [`skipWhile`] in RxJava.

[`dropWhile`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@dropWhile(predicate:T=>Boolean):rx.lang.scala.Observable[T]
[`skipWhile`]: http://reactivex.io/RxJava/javadoc/rx/Observable.html#skipWhile(rx.functions.Func1)

![dropWhile](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/skipWhile.png)

```scala
Observable.just(1, 2, 3, 4, 5)
  .dropWhile(_ < 3)
  .subscribe(println(_))
// prints: 3, 4, 5
```


`take`
------

Similar to `drop`, there is a [`take(n)`] operator which only keeps the first `n` elements of the stream and drops all
elements after that. Notice in the diagram below that, because after `n` elements no more elements will follow, the
`Observable` completes earlier than usual, that is: immediately after the `n`th element is emitted.

[`take(n)`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@take(n:Int):rx.lang.scala.Observable[T]

![take](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/take.png)

```scala
Observable.just(0, 1, 2, 3, 4)
  .take(3)
  .subscribe(println(_))
// prints: 0, 1, 2
```

Similar to a `dropWhile` there is a [`takeWhile`] that takes a predicate and returns a stream with all elements of the
original stream as long as the predicate stays `true`. Once the predicate becomes `false` on an element, the stream completes
and no more elements are evaluated.

[`takeWhile`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@takeWhile(predicate:T=>Boolean):rx.lang.scala.Observable[T]

![takeWhile](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/takeWhile.png)


Side effects
------------

So far we have seen a couple of operators that transform, filter or limit a stream of events/data. These operators are
supposed to run in a *pure* context, meaning that they are not allowed to have side effects such as writing to standard
output, a file or a database, mutating state, or putting a thread to sleep. This is all part of the functional style
that is incorporated in the Rx libraries.

To perform side effects, we have special methods to our disposal to do so. These are usually identified by the name of
the event on which's occurrence the side effect needs to take place, prefixed with `do`. Each operator takes a function as its
argument, which is executed whenever the specific event occurs. Note that the event is passed on to the next operator after the
function is executed.

* [doOnNext] - takes a function of type `T => Unit` that is applied with every element of the stream, after which the
  original element is passed on downstream.
* [doOnError] - takes a function of type `Throwable => Unit` and applies it to the *Error* event that might terminate the stream.
* [doOnCompleted] - executes the code block given as argument whenever the stream encounters a *Completed* event.
* [doOnSubscribe] - executes the code block given as argument whenever the stream is subscribed to by any `Observer`.
* [doOnUnsubscribe] - executes the code block given as argument whenever the stream is unsubcribed from.
* [doOnTerminate] - executes the code block given as argument whenever the stream encounters either a *Completed* or *Error* event.
* [doAfterTerminate] - executes the code block given as argument after the stream is unsubscribed from.

[doOnNext]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnNext(onNext:T=>Unit):rx.lang.scala.Observable[T]
[doOnError]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnError(onError:Throwable=>Unit):rx.lang.scala.Observable[T] 
[doOnCompleted]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnCompleted(onCompleted:=>Unit):rx.lang.scala.Observable[T]
[doOnSubscribe]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnSubscribe(onSubscribe:=>Unit):rx.lang.scala.Observable[T]
[doOnUnsubscribe]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnUnsubscribe(onUnsubscribe:=>Unit):rx.lang.scala.Observable[T]
[doOnTerminate]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doOnTerminate(onTerminate:=>Unit):rx.lang.scala.Observable[T]
[doAfterTerminate]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@doAfterTerminate(action:=>Unit):rx.lang.scala.Observable[T]

One frequent use case for these operators is debugging inside an operator sequence. With these `doOnXXX` operators you can find
out what elements are going through the operator sequence.

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

Another use case for `doOnNext` is to perform a certain side effect to each element in the `Observable`, such as adding the elements
to a `Queue` or `Map`. This is a particular example from [`easy-pid-generator`], where each element in an `Observable` of JSON `String`s
is put into a concurrent `Map` before it is potentially processed further in later stages of the operator sequence. This is set up in
such a way that another process can read values from this `Map` that were put there using the `doOnNext`.

A simplification of a use case like this is shown below. Here an arbitrary `Observable` is queue-ing its data before continuing with
the rest of its procedure. Another process, that is interested in this data, can pull the data out of the queue at its own pace or wait
for a next element to be put in the queue.

```scala
val queue = new mutable.Queue[Int]
Observable.just(1, 2, 3, 4)
  .map(_ * 2) // or any other sequence of operators
  .doOnNext(queue.enqueue(_))
  .filter(_ % 4 == 0) // or any other sequence of operators
  .subscribe(i => println(s"even number: $i"))
```

[`easy-pid-generator`]: https://github.com/rvanheest-DANS-KNAW/easy-pid-generator/blob/087269956a0201ab67edbcf7a793ac17e26dfd3c/src/main/scala/nl/knaw/dans/easy/pid/microservice/PidHazelcastService.scala#L62

A third and final use case of these operators can be found in [`easy-pid-generator` as well]. In this instance the main program
can only terminate once the `Observable` is completed. In order to do so, we use `doOnCompleted` to signal that it is safe to
terminate the main program.

[`easy-pid-generator` as well]: https://github.com/rvanheest-DANS-KNAW/easy-pid-generator/blob/087269956a0201ab67edbcf7a793ac17e26dfd3c/src/main/scala/nl/knaw/dans/easy/pid/microservice/PidHazelcastService.scala#L58

Finally, the careful reader may already have noticed some overlap between the `subscribe` method and the `doOnNext`, `doOnError`
and `doOnCompleted`. The functions in these `doOnXXX` operators can be replaced with the arguments of the `subscribe` method.
For example: `obs.doOnNext(println(_)).subscribe` is equivalent to `obs.subscribe(println(_))`. As the latter achieves the same
result with fewer operators, this one is preferred over the former. On the other hand, `obs.doOnError(_.printStackTrace()).subscribe`
can also be written as `obs.subscribe(_ => {}, _.printStackTrace())`, but requires the extra boilerplate code of `_ => {}`, since
the `onError` handler is the second argument of `subscribe`. As to date there are no definitive style guides on RxJava/RxScala, and
it is therefore a matter of taste which way of writing to choose.


Error handling
--------------

As mentioned before, *Error* is a terminal event, meaning that once this event happens, no other events like *Next*, *Error* or
*Completed* can ever occur in this stream. However, in production level systems it is not expected that a service terminates itself
whenever an error occurs; especially when it is not a fatal error. Preferably the error is logged, after which the stream recovers
itself and carries on.

This is exactly what the [`retry`] operators can do. When included in the operator sequence, they consume the error, do
not propagate it and restart the upstream `Observable`. This latter bit is important, since the upstream `Observable` *terminated*
with an exception. If no parameter is given to `retry`, it will retry forever, without ever propagating an error. When
[specified as an argument], the `retry` will only happen a finite amount of times, after which the latest error is still propagated.

[`retry`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@retry:rx.lang.scala.Observable[T]
[specified as an argument]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@retry(retryCount:Long):rx.lang.scala.Observable[T]

![retry](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/retry.png)

In [`easy-ingest-dispatcher`] the `retry` is included in the [`run` method] to ensure that the `jobMonitor` always keeps
running, even if an error occurs. Since `retry` consumes the exception, it is a good practice to have a `doOnError` right
before it, that logs the exception.

```scala
observableThatMayOrMayNotThrowSomeKindOfError // the error is propagated as an onError event
  .doOnError(e => log.error(s"error ocurred: ${e.getMessage}", e)) // the error is logged and propagated again
  .retry // the error is consumed (NOT propagated!!!) and the stream starts again
  // some more operators
  .subscribe(/* do something */)
```

[`run` method]: https://github.com/rvanheest-DANS-KNAW/easy-ingest-dispatcher/blob/7f802ad62fa78a74b628a43f58f050599c2ce59d/src/main/scala/nl/knaw/dans/easy/ingest_dispatcher/EasyIngestDispatcher.scala#L63

Another way of dealing with an *Error* event is by using [`onErrorResumeNext`]. This operator takes a function of type
`Throwable => Observable[T]`, where the `Throwable` is the exception encountered in the `onError` event and `Observable[T]`
is the stream with which to continue the operator sequence.

[`onErrorResumeNext`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@onErrorResumeNext[U>:T](resumeFunction:Throwable=>rx.lang.scala.Observable[U]):rx.lang.scala.Observable[U]

![onErrorResumeNext](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/onErrorResumeNext.png)

Again it is often useful to first log the error using `doOnError` before consuming it in `doOnErrorResumeNext`.

```scala
Observable.error(new Exception("useful error message"))
  .doOnError(println(s"onError: ${_.getMessage}"))
  .onErrorResumeNext(e => Observable.just(-1))
  .subscribe(println(_))
```


`repeat`
--------

While `retry` recovers an `Observable` from terminating with an exception, the [`repeat`] operator recovers an `Observable` from
terminating with a *Completed* event. This is particularly useful when creating infinite streams of events from finite `Observable`s.
For example, if you want to have an infinite stream of `0` values, you can achieve this by defining an `Observable` that only emits
one `0` and calling `repeat` on it to make the stream produce infinitely many values: `Observable.just(0).repeat`.

[`repeat`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@repeat:rx.lang.scala.Observable[T]

![repeat](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/repeat.o.png)


An example
----------

There are many more operators that could be discussed in this section. The operators listed above are the ones that are
most notably used in the current code of [EASY]. In this final section on operators we will look at an example
of using an `Observable`. We will create a stream of numbers that form the [Fibonacci sequence]. Starting of with the
values `{0, 1}`, this sequence adds up the previous two values as its next value: `fib(n) = fib(n - 1) + fib(n - 2)`.
So, this first elements of this (infinite) sequence are: `{1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, ...}`

To write this in terms of `Observable`, we start with an `Observable.just(0).repeat` that will emit an infinite stream of
elements (in this case they are all `0`). We will not actually use the value itself, but rather use the fact that an
element is emitted (i.e. an `onNext` occurred). 
  
The accumulation phase is done using a `scan` operation: `.scan((0, 1)) { case ((pp, p), _) => (p, pp + p) }`. Here we use
a *tuple* as the seed value: `(0, 1)`. To get the next element of the sequence, we calculate the sum of these values (`pp + p`)
and tuple that with the latest value (`p`). Note that we discard the original elements (which were all `0`s) by using an `_`.
The resulting tuple will then become the next seed value *and* be emitted downstream for further use.

The resulting stream therefore has type `Observable[(Int, Int)]`, where each tuple contains the *previous* and *current* number
in the sequence. Since we're only interested in the *current* value, we have to `map` the stream and get out the right-hand
side of the tuple: `.map(_._2)`.

In the full code example below we define the function `fibonacci` to be this infinite stream of values. It is good practice to
leave out any bounds on the number of elements (i.e. how many of the elements in the sequence do you want) in this function
and let the user decide how many he wants. Some might be interested in the first `n` elements of the sequence, others might
be interested in all elements less then `n` or all *even* elements less than a certain number `n`...

Below is a table of the values that are emitted by each of the operators in this function. This makes it especially clear
what the `scan` operator is doing.

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
    .map { case (_, i) => i }
}

fibonacci.take(10).subscribe(println(_))
fibonacci.takeWhile(_ < 100).subscribe(println(_))
fibonacci.filter(_ % 2 == 0).takeWhile(_ < 1000).subscribe(println(_))
```

This code can also be found [here], for you to play with.

[here]: assignments/Fibonacci.scala


Assignment
----------

At the end of this section you should be able to do the [Pi Approximation] assignment.

[Pi Approximation]: ./assignments/PiApproximation.md
