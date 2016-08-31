#Try

In Java you have to state explicitly that a certain method can throw an exception. That is, if the exception is not a `RuntimeException`. When this exception throwing method is used in a second method, it must handle the exception by either propagating it up the stack (rethrowing) or catching it and providing an alternative value.

```java
public static void foo throws Exception {
    throw new Exception();
}

public static void bar1() throws Exception {
    foo();
}

public static void bar2() {
    try {
        foo();
    }
    catch (Exception e) {
        // do something
    }
}
```

In Scala you can throw exceptions as well, but there is no specification such as Java's `throws Exception` in the function declaration. This makes the type of your functions less safe and can cause unexpected bugs and failures. The compiler cannot check whether an error can occur somewhere and is therefore not able to warn you about it.

```scala
def foo: Unit = {
    throw new Exception()
}

// if this method is not documented properly and/or you don't have access
// to the source code, you will only find out at runtime that this function
// throws an Exception. This is not visible in the return type!
```

Scala instead provides a technique that is quite similar to its `Option` type. Whereas this type denoted the possibility of returning a `null`, a `Try` type expresses the possibility of returning an `Exception` instead of an actual value.

`Try` has two subclasses: `Success` (which holds an actual value) and `Failure` (which holds an exception). The approach here is to not *throw* the `Exception` but *return* it, wrapped in a `Failure` class.

```scala
def funckySum(a: Int, b: Int): Try[Int] = {
  val sum = a + b
  if (sum == 5) Success(5)
  else Failure(IllegalArgumentException("sum isn't 5, sorry, can't return it :("))
}
```




/////////////////////////////
use the following example?
from: http://underscore.io/blog/posts/2015/02/23/designing-fail-fast-error-handling.html
/////////////////////////////
```scala
// Given a method that returns `Either`:
def readInt: Either[String, Int] =
  try {
    Right(readLine.toInt)
  } catch {
    case exn: NumberFormatException =>
      Left("Please enter a number")
  }

// We can call right-biased flatMap...
readInt.right.flatMap { number =>
}

/// ...or left-biased flatMap:
readInt.left.flatMap { errorMessage =>
  // flatMap is left-biased here
}

// This makes for-comprehensions cumbersome:
for {
  x <- readInt.right
  y <- readInt.right
  z <- readInt.right
} yield (x + y + z)
```
