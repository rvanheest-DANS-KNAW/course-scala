// translated from https://github.com/frode-carlsen/scala-workshop/blob/master/scala-workshop/src/main/scala/oppgave5/PatternMatch.scala

sealed trait Tree
case class Branch(left: Tree, right: Tree) extends Tree
case class Leaf(value: Int) extends Tree

def size(tree: Tree): Int = {
  tree match {
    case Branch(left, right) => 1 + size(left) + size(right)
    case Leaf(_) => 1
  }
}

def height(tree: Tree): Int = {
  tree match {
    case Branch(left, right) => 1 + math.max(height(left), height(right))
    case Leaf(_) => 1
  }
}

def contains(value: Int, tree: Tree): Boolean = {
  tree match {
    case Branch(left, right) => contains(value, left) || contains(value, right)
    case Leaf(v) => value == v
  }
}

val tree1 = Leaf(5)

size(tree1)
height(tree1)
contains(4, tree1)
contains(5, tree1)

val tree2 = Branch(Leaf(1), Leaf(2))

size(tree2)
height(tree2)
contains(0, tree2)
contains(1, tree2)
contains(2, tree2)

val tree3 =
  Branch(
    Branch(
      Branch(
        Leaf(0),
        Branch(
          Leaf(1),
          Leaf(2)
        )
      ),
      Leaf(3)
    ),
    Leaf(4)
  )

size(tree3)
height(tree3)
(-2 until 6).map(i => (i, contains(i, tree3))).mkString("[", ", ", "]")
