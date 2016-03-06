import ExpressionEvaluator._
import PrettyPrinter._

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

object PrettyPrinter {
	def prettyPrint(expression: Expr): String = expression match {
		case Number(value) => value.toString
		case Sum(lhs, rhs) => s"${prettyPrint(lhs)} + ${prettyPrint(rhs)}"
		case Product(lhs, rhs) =>
			def pp(expr: Expr) = expr match {
				case s: Sum => s"(${prettyPrint(s)})"
				case _ => s"${prettyPrint(expr)}"
			}
			s"${pp(lhs)} * ${pp(rhs)}"
	}
}

val expr1 = Product(Sum(Number(4), Number(3)), Number(2)) // (4 + 3) * 2
val expr2 = Product(Number(4), Sum(Number(3), Number(2))) // 4 * (3 + 2)
val expr3 = Product(Sum(Number(4), Number(3)), Sum(Number(2), Number(1))) // (4 + 3) * (2 + 1)
val expr4 = Sum(Number(4), Product(Number(3), Number(2))) // 4 + 3 * 2

val pp1 = prettyPrint(expr1)
val pp2 = prettyPrint(expr2)
val pp3 = prettyPrint(expr3)
val pp4 = prettyPrint(expr4)

val result1 = evaluate(expr1)
val result2 = evaluate(expr2)
val result3 = evaluate(expr3)
val result4 = evaluate(expr4)
