Observable
==========

Although we can model a lot collections by using an `Iterable`, it does not suffice all cases. For example, we are not able to model a collection of real-time mouse moves, key presses or button clicks like an `Iterable`. These collections do not have something like a `hasNext` and `next`. Instead, **events** come whenever they want to or when they environment generates them. Their source is something that you cannot influence, slow down, speed up or interact with in any other way. In other words, you cannot *pull* events from their source, but instead they are **pushed** to you. All you have to do is listen to the collection (often called stream) and **react** to the events that you receive.

It should be clear that there is an important distinction between these two types of collections. On the one hand we have **interactive** collections (a subtype of `Iterable`) with which you interact by **pulling** the data from the collection. You as a *consumer* are in charge of the rate at which the elements in the collection get processed, while the *producer* of the data (the `Iterable`/`Iterator` combination) has to obey your commands and return a new element only when you want to.

On the other hand we have the **reactive** collection. Examples of this can be various kinds of realtime data (mouse moves, key presses, stock price, sensor data), data that takes a long time to be computed for which you don't want to block your program flow or data that has to come from an external source (network call, file IO). These kinds of collections do not have an interactive interface and you therefore cannot pull the next element. Instead the data is **pushed** to you and you have to **react** to what you receive by processing it in some way. Therefore the *producer* is fully in charge of how fast or how slow the data is streaming towards you, while you as a *consumer* can only wait and react to what data comes in.

| Collection  | Java/Scala type | Interaction | In charge |
|-------------|-----------------|-------------|-----------|
| interactive | `Iterable`      | pull based  | consumer  |
| reactive    | `Observable`    | push based  | producer  |


Types of events
---------------
The contract of a reactive collection states that three types of events can be pushed from the producer to the comsumer.

  * The first type, **OnNext**, is the carrier of every element you receive. You can view it as a box with the element inside it. During the lifetime of a reactive collection, the producer will send 0 or more *OnNext* events to its consumer.
  * The second type of events is the **OnError** event. This event signals that something went wrong in the collection. An *OnError* event signals the sudden, exceptional 'death' of a reactive collection and after it no other event will or can ever occur! The *OnError* event carries the `Throwable` that caused its termination with it, so you can always see what caused this exceptional behavior and act accordingly.
  * Finally, the third type of event signals the normal and peaceful termination of a stream. Whenever the producer decides it will never send any *OnNext* events again, it will conclude the stream with an **OnCompleted** event. This is an empty event, it does not contain any value and can be considered as just a flag to signal to the consumer that no event will ever come after it.

In practice the *OnCompleted* and *OnError* events are a signal for the consumer to stop listening to the stream. As no event will ever come after it, there is no need to continue observing the collection!

Given these three types of events, we can derive an interface that the consumer has to obey to in order to observe the stream. It has to be able to accept each of these three events, `onNext`, `onError` and `onCompleted`. As the goal of this interface is observe a stream of events, it is called `Observer`.

```scala
trait Observer[T] {
  def onNext(value: T): Unit
  def onError(error: Throwable): Unit
  def onCompleted(): Unit
}
```

Besides that, we can also define an interface for the reactive collection itself. The only thing it has to be able to do is push events/data at the `Observer` that is listening (or subscribed) for them. In other words, it needs to be `Observable` and has to allow an `Observer` to subscribe to it.
 
```scala
trait Observable[T] {
  def subscribe(observer: Observer[T]): Subscription
}
```

Finally, the `Subscription` that is returned by the `subscribe` method is used later to unsubscribe from the `Observable` and therefore stop listening to the stream of events. `Subscription` therefore has the following interface:

```scala
trait Subscription {
  def isUnSubscribed: Boolean
  def unsubscribe(): Unit
}
```

Streams of values
-----------------
TODO continue here
