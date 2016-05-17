import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, FormatStyle}

trait Logger {
  def log(msg: String): Unit
}

class ConsoleLogger extends Logger {
  def log(msg: String) = println(msg)
}

trait ThreadLogging extends Logger {
  abstract override def log(msg: String) = {
    super.log(s"[${Thread.currentThread().getName}] $msg")
  }
}

trait DateTimeLogging extends Logger {

  val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

  abstract override def log(msg: String) = {
    super.log(s"[${LocalDateTime.now.format(formatter)}] $msg")
  }
}

val log1 = new ConsoleLogger
log1.log("hello world")

val log2 = new ConsoleLogger with ThreadLogging
log2.log("hello world")

val log3 = new ConsoleLogger with DateTimeLogging
log3.log("hello world")

val log4 = new ConsoleLogger with ThreadLogging with DateTimeLogging
log4.log("hello world")

val log5 = new ConsoleLogger with DateTimeLogging with ThreadLogging
log5.log("hello world")
