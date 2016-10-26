Combining streams
=================

An `Observable` is different from any other collection in the sense that it can have a notion of time to it. The event stream of a button in user interface does not emit events constantly; it only emits an event whenever the button is pressed. Sometimes the button is pressed many times in a short period, other times it is almost never pressed. The same holds for mouse moves, key presses and other event-based sources. More surprising sources are network responses and database query results: in a network it might take some time before the response on a request comes in; depending on the complexity of the query it may take a long time before the database has finished computing the result. Because of this notion of time in an `Observable`, there are a number of ways in which to combine various streams. In this section we will give an overview of the most notable and useful combine operators.

*Side note:* This notion of time does however not apply for all instances. For example, the primitive `Observable`s such as `Observable.just` emit their value(s) immediately, one after the other. These clearly do not have this notion of time to them!


`concat`/`++`
-------------

The simplest combine operator is [`concat`] (or `++` in RxScala). Given two arbitrary `Observable`s, `obs1` and `obs2` that emit values **of the same type**, `obs1 ++ obs2` first emits all elements from `obs1` and only starts emitting the values from `obs2` when `obs1` has had an `onCompleted`. You can view this operator as '*sequential composition*' in that it will first process everything from `obs1` and only then start processing `obs2`.

If `obs1` emits an `onError` event, `concat` will just propagate this (just as every other event), but will **not** continue with `obs2`, since `onError` is a terminating event!

[`concat`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@++[U>:T](that:rx.lang.scala.Observable[U]):rx.lang.scala.Observable[U]

![concat](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/concat.png)

```scala
val obs1 = Observable.just(1, 2, 3).doOnCompleted(println("  obs1 completed"))
val obs2 = Observable.just(4, 5, 6).doOnCompleted(println("  obs2 completed"))
(obs1 ++ obs2).subscribe(i => println(i), e => e.printStackTrace(), () => println("completed total"))

// prints: 1, 2, 3, obs1 completed, 4, 5, 6, obs2 completed, completed total
```


`merge`
-------

In contrast to `concat`, [`merge`] will process `obs1` and `obs2` at the same time ('*parallel composition*'). This operator 'listens' to both `Observable`s and emits the elements of both streams. The marble diagram below shows the case in which `obs1` terminates with an `onError` event. In this case the resulting `Observable` terminates as well and will no longer emit elements from the other stream that is still running successfully. [RxMarbles' `merge` example] on the other hand shows the successful case in which both `Observable`s terminate naturally with an `onCompleted` event. Drag around the `onNext` and `onCompleted` events to see the behavior in this case.

[`merge`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@merge[U>:T](that:rx.lang.scala.Observable[U]):rx.lang.scala.Observable[U]
[RxMarbles' `merge` example]: http://rxmarbles.com/#merge

![merge](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/merge.png)


`flatMap`
---------

The [`flatMap`] operator in Rx works in the same way as in the Scala Collections API, as discussed in [workshop 3]. It's `map` part produces a new `Observable` for each element that comes in, which are flattened into a single `Observable` using the merge operator.

The example below uses the `Observable.just` and therefore the elements in `Observable.just(i, i)` are emitted first, before the next element of `Observable.just(1, 2, 3)` is processed. This means that the elements are emitted in sequence. This is all due to the fact that `Observable.just` does not have a notion of time in it!

On the other hand, if an `Observable` has a notion of time (as shown in the marble diagram below), it will expose the same behavior as merge in that a *parallel composition* is applied.

[`flatMap`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@flatMap[R](f:T=>rx.lang.scala.Observable[R]):rx.lang.scala.Observable[R]
[workshop 3]: ../workshop3/05%20list-for-comprehensions.md

![flatMap](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/flatMap.png)

```scala
Observable.just(1, 2, 3)
  .flatMap(i => Observable.just(i, i))
  .subscribe(i => println(i))
// prints: 1, 1, 2, 2, 3, 3
```

Note that just as with other collections that have `flatMap`, you can use for-comprehensions with `Observable`s as well. In some cases this is really useful, such as in [easy-ingest-flow]! The example above can be translated into the following for-comprehension:

[easy-ingest-flow]: https://github.com/rvanheest-DANS-KNAW/easy-ingest-flow/blob/527d152f371e90c64a4104b7e666645a21cfe869/src/main/scala/nl/knaw/dans/easy/ingest_flow/MendeleyExecution.scala#L30

```scala
val forCompr = for {
  i <- Observable.just(1, 2, 3)
  j <- Observable.just(i, i)
} yield j
forCompr.subscribe(i => println(i))
// prints: 1, 1, 2, 2, 3, 3
```
