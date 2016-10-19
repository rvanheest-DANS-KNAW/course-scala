import rx.lang.scala.Observable

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .filter(i => i % 2 == 0)
  .subscribe(i => println(i))

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(i => i.toString)
  .subscribe(s => println(s))

Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  .map(i => i + 1)
  .subscribe(i => println(i))

Observable.just("81", "42", "twee", "150", "15een")
  .filter(s => s.forall(c => c.isDigit))
  .map(s => s.toInt)
  .subscribe(i => println(i))

Observable.just(1, 2, 3, 2, 4, 1, 5)
  .distinct
  .subscribe(i => println(i))

Observable.just("a", "abc", "ab", "aaa", "b")
  .distinct(s => s.length)
  .subscribe(s => println(s))

Observable.just(1, 2, 2, 3, 2, 2, 2)
  .distinctUntilChanged
  .subscribe(i => println(i))

Observable.just("a", "b", "ab", "abc", "bc")
  .distinctUntilChanged(s => s.length)
  .subscribe(s => println(s))

Observable.just(1, 2, 3, 4, 5)
  .drop(3)
  .subscribe(i => println(i))

Observable.just(1, 2, 3, 4, 5)
  .dropWhile(i => i < 3)
  .subscribe(i => println(i))
