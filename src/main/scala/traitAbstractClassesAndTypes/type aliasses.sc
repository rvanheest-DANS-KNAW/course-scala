/*
see also: "http://twitter.github.io/effectivescala/#Types and Generics-Type aliases"
 */

import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/*
  use type aliasses to have
    - be more expressiveness in the types and communicate meaning/purpose
    - less verbose code to write
 */

// example1:
// ex1 returns a List[String], but from the context it may not be clear what these Strings represent
def ex1(age: Int): List[String] = {
  // do something
  ???
}

// defining Name to be a String, your type becomes more expressive.
type Name = String
def ex1_improved(age: Int): List[Name] = {
  // do something
  ???
}

// example2:
// if we refer in this class to a queue, we always mean a ConcurrentLinkedQueue
// and a map is always a ConcurrentHashMap[K, ConcurrentLinkedQueue[V]]
// why not simplify this by adding type aliasses to
//   - communicate the purpose of these objects (being a Queue and a Map respectively)
//   - add brevity for the reader
//   - write less and do the same
class ConcurrentPool[K, V] {
  type Queue = ConcurrentLinkedQueue[V]
  type Map = ConcurrentHashMap[K, Queue]
  // do stuff
}

// example3:
// a dataset is a mapping from a key to a list of values
// multiple datasets with their corresponding ID can be in a list
type DatasetID = String
type MultiDepositKey = String
type MultiDepositValues = List[String]
type Dataset = mutable.HashMap[MultiDepositKey, MultiDepositValues]
type Datasets = ListBuffer[(DatasetID, Dataset)]

/*
  in general:
    - do not alias types that are self-explanatory
    - don't use subclassing when an alias will do:
      > don't do: trait Datasets extends ListBuffer[(DatasetID, Dataset)]
      >   but do: type Datasets = ListBuffer[(DatasetID, Dataset)]
    - a type alias is NOT a new type like you do with trait, class and object
      they are just substitutes for the aliassed type
    - the best place to define a type alias is a package object when they need to be visible to the whole package (and beyond)
      if the type alias is only used internally, define it locally in a class, object or function
 */
