import rx.lang.scala.Observable

val obs1 = Observable.just(1, 2, 3).doOnCompleted(println("  obs1 completed"))
val obs2 = Observable.just(4, 5, 6).doOnCompleted(println("  obs2 completed"))
(obs1 ++ obs2).subscribe(i => println(i), e => e.printStackTrace(), () => println("completed"))

Observable.just(1, 2, 3)
  .flatMap(i => Observable.just(i, i))
  .subscribe(i => println(i))

val forCompr = for {
  i <- Observable.just(1, 2, 3)
  j <- Observable.just(i, i)
} yield j
forCompr.subscribe(i => println(i))
