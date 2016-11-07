Wikipedia suggestion
====================

In this assignment we will build a small application with a user interface using JavaFx and RxScala. The main goal is to
become familiar with getting event streams from UI components and combining them using Rx operators to create complex behavior.

Given is the JavaFx outline of a simple application consisting of a TextField, Button and ListView. When you're done, you
can type a word into the TextField, either click the Button or press '*Enter*' on the TextField, and get back a list of
suggested topics on Wikipedia in the ListView.

The Wikipedia API call is already given, as well as the JSON parsing. See the `searchWikipedia` and `parseJSON` functions.
Just have a look at the code and try to understand what it does. Mainly focus on the understanding of `Observable.using`.
Also look in the API documentation and the marble diagram!

If you run the code as given here, you will see the JavaFx screen, but without any functionality on the Button or TextField.
That is what you are going to implement in this assignment using RxScala and the RxJavaFx extension. In the steps below,
keep in mind that RxJavaFx returns a RxJava `Observable`, which you have to convert to a RxScala `Observable`.

1. Get the stream of Button's events (`ActionEvent.ACTION`) using `JavaFxObservable.fromNodeEvents`.
2. Also get the `textProperty` from the TextField and make it a stream using `JavaFxObservable.fromObservableValue`.
3. Combine these streams using the appropriate combinator operator. The result should be an `Observable[String]` containing
   the text in the TextField. To test this, you can subscribe to this combined stream and print the result to the console.
4. You only want to search for a particular `String` if it is not empty and if it was distinct from the previous one. So,
   a stream of OnNext events like `'abc' - 'def' - '' - 'abc' - 'abc'` results in `'abc' - 'def' - 'abc'`. Add two operators
   to the combined stream that give you this additional behavior. You can use Apache's `StringUtils.isBlank` (or a variant)
   to check if a `String` is blank.
5. Now that you have only the search `String`s you want, you can feed them to `searchWikipedia`. This method's signature
   will tell you which operator you need to use.
6. Subscribe to the resulting `Observable` by calling `items.clear()` followed by `results.foreach(items.add)`, where
   `results` is a `List` emitted by the `searchWikipedia` `Observable`. You might want to also add event handlers for
   `onError` and `onCompleted`. Allocate the resulting `Subscription` to the value `searchSubscription`.

After this you should be able to interact with the application as intended. Type something (for example 'hello') in the
TextField; click the Button and see the suggested topics on Wikipedia coming in to the ListView.

The `searchSubscription` defined in (6) is used in the code to unsubscribe when the application is going to shut down.
Give it a look and reason about why you need this.

With this basic behavior, you can now add all kinds extra functionality. Here are a couple of suggestions:

* You also want to trigger a search whenever 'enter' is pressed on the TextField. This is done by
  `JavaFxObservable.fromNodeEvents(textField, ActionEvent.ACTION)`. Where would you add this to the sequence?
  Hint: what's the return type of this stream and where do you see the same type of stream? Which operator would you use for this?
* Sometimes the internet is down. In this case the stream emits a terminating `onError` event. Because of this you cannot
  have anymore interaction with the application. What you can do instead is resubscribe automatically whenever an error occurs.
  Use the `retry` operator for this. However, now the error callback does not get triggered in the `subscribe` anymore.
  How would you fix this?
