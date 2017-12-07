Ideas workshop 6 - intro to `better.files`
==========================================

Alternative to `java.io.File` and `java.nio.file.Path`
Most info in the [README].

Important reasons for switching to `better.files`:
* native Scala library rather than a Java library
* follow Scala standards (this lib will be incubated/integrated into the Scala Platform)
* more natural syntax for traversing a filesystem (including UNIX operators)
* automatic resource management (no more fiddling around with `java.util.stream.Stream` closing and converting to Scala structures)
* `better.files`' paths are absolute by default; it uses `java.nio.file.Path` for relative paths


Workshop setup
--------------

* Short intro to look through the [README]; point out the most basic operators, such as:
    * [Instantiation] and path traversal with `/`
    * [I/O] and the fluent API
    * [Streaming] and [resource management] (mainly the `ManagedResource` part)
    * [Java interoperability] - required for interaction with Java APIs
* Assignment
    * Given is a program that walks over a series of bag stores and gathers some information about
      the stores, bags and files in `data/`. It then constructs a report that is written to file.
      The program is written using the `java.nio.file.Path` API.
    * Rewrite the program such that it uses the `better.files` API instead.
    * Goals for this assignment
        * Get to know the `better.files` API
        * Experience how much shorter and more elegant you can make your program using `better.files`


[README]: https://github.com/pathikrit/better-files/blob/master/README.md
[Instantiation]: https://github.com/pathikrit/better-files/blob/master/README.md#instantiation
[Java interoperability]: https://github.com/pathikrit/better-files/blob/master/README.md#java-interoperability
[I/O]: https://github.com/pathikrit/better-files/blob/master/README.md#file-readwrite
[Streaming]: https://github.com/pathikrit/better-files/blob/master/README.md#streams
[resource management]: https://github.com/pathikrit/better-files/blob/master/README.md#lightweight-arm
