trait Expr
case class Number(value: Int) extends Expr
case class Sum(lhs: Expr, rhs: Expr) extends Expr
case class Product(lhs: Expr, rhs: Expr) extends Expr

object ExpressionEvaluator {
	def evaluate(expression: Expr): Int = expression match {
		case Number(value) => value
		case Sum(lhs, rhs) => evaluate(lhs) + evaluate(rhs)
		case Product(lhs, rhs) => evaluate(lhs) * evaluate(rhs)
	}
}

val expr = Product(Sum(Number(4), Number(3)), Number(2)) // (4 + 3) * 2
val result = ExpressionEvaluator.evaluate(expr)
