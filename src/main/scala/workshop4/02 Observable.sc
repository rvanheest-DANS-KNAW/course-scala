import rx.lang.scala.{Observable, Observer}

val emit123: Observable[Int] = Observable.just(1, 2, 3)
val emitFromList: Observable[Int] = Observable.from(List(1, 2, 3, 4))
val emitError: Observable[Int] = Observable.error(new Exception("something went wrong"))
val emitEmpty: Observable[Int] = Observable.empty
val emitNever: Observable[Int] = Observable.never

def observer: Observer[Int] = new Observer[Int] {
  override def onNext(value: Int) = println(s"received onNext event with value: $value")

  override def onError(error: Throwable) = println(s"""received onError event of type ${error.getClass.getSimpleName} and message "${error.getMessage}"""")

  override def onCompleted() = println("received onCompleted event")
}

emit123.subscribe(observer)

emitFromList.subscribe(observer)

emitError.subscribe(observer)

emitEmpty.subscribe(observer)

emitNever.subscribe(observer)

emit123.subscribe(
  value => println(s"received onNext event with value: $value"),
  error => println(s"""received onError event of type ${error.getClass.getSimpleName} and message "${error.getMessage}""""),
  () => println("received onCompleted event")
)

emit123.subscribe(
  value => println(s"received onNext event with value: $value")
)

emitEmpty.subscribe(
  value => println(s"received onNext event with value: $value")
)
