Discard every third element
===========================

1. Implement the function `discardThirdBuffer` that takes an `Observable[T]` called `obs` and returns an `Observable[T]`
   that *skips every third element* that is emitted by `obs`.
    * use one of [RxJava's `buffer` operators] and look up its [RxScala equivalent]
    * hint: besides that you need two more operators. One of them is used to flatten the `Observable[Seq[T]]` to a
    `Observable[T]`; use [`flatMapIterable`].
2. One of the operators you have used, may or may not have been either [`Seq.take`] or [`Seq.dropRight`]. Does it make
   any difference which one to use?
3. Implement the function `discardThirdWindow` that an `Observable[T]` called `obs` and returns an `Observable[T]` that
   *skips every third element* that is emitted by `obs`.
    * use one of [RxJava's `window` operators] and look up its [RxScala equivalent].
    * hint: Besides that you need two more operators. One of them is to flatten the `Observable[Observable[T]]` to a
      `Observable[T]`.
4. One of the operators you have used, may or may not have been either [`Observable.take`] or [`Observable.dropRight`].
   Given their respective marble diagrams, argue if it matters which one to use, and if so argue which of these is the best
   one to use in this context.
5. You have implemented `discardThird` using both `buffer` and `window`. Using the marble diagrams of these respective
   operators, argue which one is the best one to use. Hint: think about at which time each elements gets emitted in the
   respective cases.

[RxJava's `buffer` operators]: http://reactivex.io/RxJava/javadoc/rx/Observable.html#buffer(rx.functions.Func0)
[RxScala equivalent]: http://reactivex.io/rxscala/comparison.html
[`flatMapIterable`]: http://reactivex.io/RxJava/javadoc/rx/Observable.html#flatMapIterable(rx.functions.Func1)
[`Seq.take`]: http://www.scala-lang.org/api/current/scala/collection/Seq.html#take(n:Int):Repr
[`Seq.dropRight`]: http://www.scala-lang.org/api/current/scala/collection/Seq.html#dropRight(n:Int):Repr
[RxJava's `window` operators]: http://reactivex.io/RxJava/javadoc/rx/Observable.html#window(rx.functions.Func0)
[`Observable.take`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@take(n:Int):rx.lang.scala.Observable[T]
[`Observable.dropRight`]: http://reactivex.io/rxscala/scaladoc/index.html#rx.lang.scala.Observable@dropRight(n:Int):rx.lang.scala.Observable[T]
