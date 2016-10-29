Combining streams
=================

An `Observable` is different from an `Iterable` in the sense that it can have a notion of time to it. The event
stream of a button in user interface does not emit events constantly; it only emits an event whenever the button is clicked.
Sometimes the button is clicked many times in a short period, other times it remains inactive for a while. The same holds for
mouse moves, key presses and other event-based sources. More surprising sources are network responses and database query
results: in a network it might take some time before the response on a request comes in; depending on the complexity of
the query it may take a long time before the database has finished computing the result. Because of this notion of time
in an `Observable`, there are a number of ways in which to combine various streams. In this section we will give an
overview of the most notable and useful combine operators.

*Side note:* This notion of time does however not apply for all instances. For example, the primitive `Observable`s such
as `Observable.just` emit their value(s) immediately, one after the other. These clearly do not have this notion of time
to them!


`concat`/`++`
-------------

The simplest combine operator is [`concat`] (or `++` in RxScala). Given two arbitrary `Observable`s, `obs1` and `obs2`
that emit values **of the same type**, `obs1 ++ obs2` first emits all elements from `obs1` and only starts emitting the
values from `obs2` when `obs1` has had an `onCompleted`. You can view this operator as '*sequential composition*' in that
it will first process everything from `obs1` and only then start processing `obs2`.

If `obs1` emits an `onError` event, `concat` will just propagate this (just as every other event), but will **not** continue
with `obs2`, since `onError` is a terminating event!

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

In contrast to `concat`, [`merge`] will process `obs1` and `obs2` at the same time ('*parallel composition*'). This operator
'listens' to both `Observable`s and emits the elements of both streams. The marble diagram below shows the case in which `obs1`
terminates with an `onError` event. In this case the resulting `Observable` terminates as well and will no longer emit elements
from the other stream that is still running successfully. [RxMarbles' `merge` example] on the other hand shows the successful
case in which both `Observable`s terminate naturally with an `onCompleted` event. Drag around the `onNext` and `onCompleted`
events to see the behavior in this case.

[`merge`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@merge[U>:T](that:rx.lang.scala.Observable[U]):rx.lang.scala.Observable[U]
[RxMarbles' `merge` example]: http://rxmarbles.com/#merge

![merge](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/merge.png)


`flatMap`
---------

The [`flatMap`] operator in Rx works in the same way as in the Scala Collections API, as discussed in [workshop 3]. It's `map`
part produces a new `Observable` for each element that comes in, which are flattened into a single `Observable` using the merge
operator.

The example below uses the `Observable.just` and therefore the elements in `Observable.just(i, i)` are emitted first, before
the next element of `Observable.just(1, 2, 3)` is processed. This means that the elements are emitted in sequence. This is
all due to the fact that `Observable.just` does not have a notion of time in it!

On the other hand, if an `Observable` has a notion of time (as shown in the marble diagram below), it will expose the same
behavior as merge in that a *parallel composition* is applied.

[`flatMap`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@flatMap[R](f:T=>rx.lang.scala.Observable[R]):rx.lang.scala.Observable[R]
[workshop 3]: ../workshop3/05%20list-for-comprehensions.md

![flatMap](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/flatMap.png)

```scala
Observable.just(1, 2, 3)
  .flatMap(i => Observable.just(i, i))
  .subscribe(i => println(i))
// prints: 1, 1, 2, 2, 3, 3
```

Note that just as with other collections that have `flatMap`, you can use for-comprehensions with `Observable`s as well. In
some cases this is really useful, such as in [easy-ingest-flow]! The example above can be translated into the following
for-comprehension:

[easy-ingest-flow]: https://github.com/rvanheest-DANS-KNAW/easy-ingest-flow/blob/527d152f371e90c64a4104b7e666645a21cfe869/src/main/scala/nl/knaw/dans/easy/ingest_flow/MendeleyExecution.scala#L30

```scala
val forCompr = for {
  i <- Observable.just(1, 2, 3)
  j <- Observable.just(i, i)
} yield j
forCompr.subscribe(i => println(i))
// prints: 1, 1, 2, 2, 3, 3
```


---
So far we have discussed a number of ways in which `Observable`s were concatenated or merged. The elements in the `onNext`s
were however always propagated 'as is', without modifying them. In some cases, however, you want to combine two streams in
such a way that their elements are grouped together. The following three sections will discuss different ways in which you
can combine streams in this way.


`combineLatestWith`
-------------------

One way to combine elements in a stream is by combining the latest element from `obs1` with the latest element from `obs2`,
called [`combineLatestWith`]. Whenever `obs1` or `obs2` emits a new value, a new 'combined' element is emitted in the resulting
stream with this newest value from that `Observable` and the latest value from the other `Observable`. Combining these element
is done using a function that you provide as an argument of the operator.

```scala
// for any arbitrary obs1 and obs2
obs1.combineLatestWith(obs2)((i1, i2) => s"i1 = $i1, i2 = $i2")
```

Although it is seen as an operator in RxScala, RxJava provides this operator as a static method on `Observable` and calls
it `combineLatest` instead. There you write:

```java
// for any arbitrary obs1 and obs2
Observable.combineLatest(obs1, obs2, (i1, i2) -> "i1 = " + i1 + ", i2 = " + i2)
```

See also [RxMarbles' `combineLatest` example] for a more interactive experience of this operator.

[`combineLatestWith`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@combineLatestWith[U,R](that:rx.lang.scala.Observable[U])(selector:(T,U)=>R):rx.lang.scala.Observable[R]
[RxMarbles' `combineLatest` example]: http://rxmarbles.com/#combineLatest

![combineLatestWith](https://raw.github.com/wiki/ReactiveX/RxJava/images/rx-operators/combineLatest.png)


`withLatestFrom`
----------------

While `combineLatestWith` calculates a new combination whenever `obs1` or `obs2` emits a new element, there is another
operator that does a variation on this. `withLatestFrom` only calculates a new combination whenever `obs1` emits a new
element and combines this with the latest element from `obs2`. However, when `obs2` emits a new element **no new
combination is emitted**!

In contrast to `combineLatestWith` there is no difference here between RxScala and RxJava. Both use `withLatestFrom` as follows:

```scala
// for any arbitrary obs1 and obs2
obs1.withLatestFrom(obs2)((i1, i2) => s"i1 = $i1, i2 = $i2")
```

See also [RxMarbles' `withLatestFrom` example] for a more interactive experience of this operator.

[`withLatestFrom`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@withLatestFrom[U,R](other:rx.lang.scala.Observable[U])(resultSelector:(T,U)=>R):rx.lang.scala.Observable[R]
[RxMarbles' `withLatestFrom` example]: http://rxmarbles.com/#withLatestFrom

![withLatestFrom](https://raw.github.com/wiki/ReactiveX/RxJava/images/rx-operators/withLatestFrom.png)


An example
----------

To demonstrate the use of `combineLatestWith` and `withLatestFrom`, have a look at the [`Login`] demo application.
This is a JavaFx application that contains two textfields for *username* and *password* respectively (for this demo
application we don't bother with obfuscating the *password* field), a button that logs in to an arbitrary thing and
a label in which the result of the login is shown. For the login procedure we have a simple case class `Credentials`
that holds a *username* and *password* combination. The idea is to combine the events from the textfields into a
`Credentials` object and combine this with the button clicks to do a login. For demo sake we will print the `Credentials`
object on the screen whenever a login happens.

First of all we require the event streams (`Observable`s) from the various UI components. For this we use the [RxJavaFx]
library that provides the necessary wrappers. Since these return RxJava `Observable`s, we have to convert them to RxScala
using the `asScala` operator. For the `Observable`s below, `usernameObs` and `passwordObs` contain the streams of text
events such that for every character changed in the textfield, the new `String` is emitted. `buttonObs` contains a stream
of button click events.

[`Login`]: assignments/Login.scala
[RxJavaFx]: https://github.com/ReactiveX/RxJavaFX

```scala
val usernameObs: Observable[String] = JavaFxObservable.fromObservableValue(usernameTF.textProperty()).asScala
val passwordObs: Observable[String] = JavaFxObservable.fromObservableValue(passwordTF.textProperty()).asScala
val buttonObs: Observable[ActionEvent] = JavaFxObservable.fromNodeEvents(button, ActionEvent.ACTION).asScala
```

To combine *username* and *password* into a `Credentials` object, the `usernameObs` and `passwordObs` streams need to be
combined using a `combineLatestWith`. The function provided takes the latest `username` and the latest `password` and
combines them into a `Credentials` object.
Then, to only get the latest `Credentials` objects before a button click, the `buttonObs` and `credentialsObs` are
combined using a `withLatestFrom`. Note that the function that combines these two events discards the event and only
uses the `creds`.

```scala
val credentialsObs = usernameObs.combineLatestWith(passwordObs)((username, password) => Credentials(username, password))
val logins = buttonObs.withLatestFrom(credentialsObs)((_, creds) => creds)
```

Finally, to get this all working, we need to subscribe to this stream of credentials, called `logins`. For this demo
application we choose to print the `creds` object on screen in a label below the button. 

```scala
logins.subscribe(creds => label.setText(s"latest login: $creds"))
```

Play around a bit with the application and maybe add a couple of `doOnNext(x => println(x))` lines to see what is going
on at each moment in time.  
