import rx.lang.scala.Observable

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .filter(i => i % 2 == 0)
  .subscribe(i => println(i))

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(i => s"'$i'")
  .subscribe(s => println(s))

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(i => i + 1)
  .subscribe(i => println(i))

Observable.just("81", "42", "twee", "150", "15een")
  .filter(s => s.forall(c => c.isDigit))
  .map(s => s.toInt)
  .subscribe(i => println(i))
