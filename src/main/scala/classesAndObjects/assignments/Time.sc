class Time(val hours: Int, val minutes: Int) {
  require(hours >= 0 && hours < 24)
  require(minutes >= 0 && minutes < 60)

  def isBefore(time: Time): Boolean = {
    (hours < time.hours) || (hours == time.hours && minutes < time.minutes)
  }
  def <(time: Time): Boolean = isBefore(time)

  override def toString = {
    def format(num: Int): String = f"$num%02d"

    s"${format(hours)}:${format(minutes)}"
  }
}
object Time {
  def apply(hours: Int, minutes: Int) = new Time(hours, minutes)
  def apply(time: String) = {
    val Array(h, m) = time.split(":")
    new Time(h.toInt, m.toInt)
  }
}

val t1 = Time(8, 52)
val t2 = Time("15:04")

t1 < t2
t1.isBefore(t2)

t2 < t1
t2.isBefore(t1)

case class Time2(hours: Int, minutes: Int) {
  require(hours >= 0 && hours < 24)
  require(minutes >= 0 && minutes < 60)

  def isBefore(time: Time): Boolean = {
    (hours < time.hours) || (hours == time.hours && minutes < time.minutes)
  }
  def <(time: Time): Boolean = isBefore(time)

  override def toString = {
    def format(num: Int): String = f"$num%02d"

    s"${format(hours)}:${format(minutes)}"
  }
}
object Time2 {
  def apply(time: String) = {
    val Array(h, m) = time.split(":")
    new Time2(h.toInt, m.toInt)
  }
}

val t3 = Time2("15:05")
val t4 = Time2(16, 32)
