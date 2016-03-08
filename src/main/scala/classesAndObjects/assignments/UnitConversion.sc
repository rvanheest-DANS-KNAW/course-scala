import Conversions._

object Conversions {
  def inchesToCentimeters(inches: Double) = inches * 2.54

  def gallonsToLiters(gallons: Double) = gallons * 3.78541

  def milesToKilometers(miles: Double) = miles * 1.60934
}

inchesToCentimeters(20.5)
gallonsToLiters(12.3)
milesToKilometers(321)

class UnitConversion(val conversionFactor: Double) {
  def apply(value: Double) = value * conversionFactor
}
object InchesToCentimeters extends UnitConversion(2.54)
object GallonsToLiters extends UnitConversion(3.78541)
object MilesToKilometers extends UnitConversion(1.60934)

InchesToCentimeters(20.5)
GallonsToLiters(12.3)
MilesToKilometers(321)
