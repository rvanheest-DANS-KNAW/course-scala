package traitAbstractClassesAndTypes.assignments

import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, FormatStyle}

trait Logger {
  def display(msg: String): String = msg

  def output(msg: String): Unit

  def info(msg: String): Unit = output(s"[INFO] ${display(msg)}")

  def error(msg: String): Unit = output(s"[ERROR] ${display(msg)}")

  def debug(msg: String): Unit = output(s"[DEBUG] ${display(msg)}")
}

class ConsoleLogger extends Logger {
  def output(msg: String): Unit = println(msg)
}

class FileLogger extends Logger {
  def output(msg: String) = println(s"<<<in a file>>>$msg")
}

trait ThreadLogging extends Logger {
  abstract override def display(msg: String) = {
    super.display(s"[${Thread.currentThread().getName}] $msg")
  }
}

trait DateTimeLogging extends Logger {

  val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

  abstract override def display(msg: String) = {
    super.display(s"[${LocalDateTime.now.format(formatter)}] $msg")
  }
}

object LoggerTest extends App {
  val log1 = new ConsoleLogger
  log1.info("hello world")

  val log2 = new ConsoleLogger with ThreadLogging
  log2.info("hello world")

  val log3 = new ConsoleLogger with DateTimeLogging
  log3.info("hello world")

  val log4 = new ConsoleLogger with ThreadLogging with DateTimeLogging
  log4.info("hello world")

  val log5 = new FileLogger with DateTimeLogging with ThreadLogging
  log5.info("hello world")

  class DefaultLogger extends ConsoleLogger with ThreadLogging with DateTimeLogging

  val theLog = new DefaultLogger
  theLog.debug("debug message")
  theLog.error("error message")
  theLog.info("info message")
}
