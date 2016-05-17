sealed abstract class Tree
case class Node(left: Tree, right: Tree) extends Tree
case class Leaf(value: Int) extends Tree
case object EmptyLeaf extends Tree

def size(tree: Tree): Int = tree match {
  case Node(l, r) => 1 + size(l) + size(r)
  case Leaf(_) => 1
  case EmptyLeaf => 0
}

def depth(tree: Tree): Int = tree match {
  case Node(l, r) => 1 + math.max(depth(l), depth(r))
  case Leaf(_) => 1
  case EmptyLeaf => 0
}

def maxValue(tree: Tree): Int = tree match {
  case Node(l, r) => math.max(maxValue(l), maxValue(r))
  case Leaf(v) => v
  case EmptyLeaf => Integer.MIN_VALUE
}

def occurs(tree: Tree, value: Int): Boolean = tree match {
  case Node(l, r) => occurs(l, value) || occurs(r, value)
  case Leaf(v) => v == value
  case EmptyLeaf => false
}

// pattern matching multiple things: http://stackoverflow.com/questions/7209728/how-to-pattern-match-multiple-values-in-scala
def numberOfLeaves(tree: Tree): Int = tree match {
  case Node(l, r) => numberOfLeaves(l) + numberOfLeaves(r)
  case Leaf(_) | EmptyLeaf => 1
}

def isBalanced(tree: Tree): Boolean = tree match {
  case Node(l, r) => math.abs(numberOfLeaves(l) - numberOfLeaves(r)) <= 1 && isBalanced(l) && isBalanced(r)
  case Leaf(_) | EmptyLeaf => true
}

val treeA = Node(EmptyLeaf, Leaf(5))
val treeB = Node(Node(Leaf(2), Leaf(7)), Leaf(5))
val treeC = Node(treeB, EmptyLeaf)

size(treeA)
depth(treeA)
maxValue(treeA)
occurs(treeA, 5)
occurs(treeA, 1)
numberOfLeaves(treeA)
isBalanced(treeA)

size(treeB)
depth(treeB)
maxValue(treeB)
occurs(treeB, 5)
occurs(treeB, 1)
numberOfLeaves(treeB)
isBalanced(treeB)

size(treeC)
depth(treeC)
maxValue(treeC)
occurs(treeC, 5)
occurs(treeC, 1)
numberOfLeaves(treeC)
isBalanced(treeC)
