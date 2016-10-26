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
