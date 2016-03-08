# UnitConversion
1. Create an object `Conversions` with methods inchesToCentimeters, gallonsToLiters and milesToKilometers, all of type `Double => Double`
2. This object is not really practical. If you have no write access to the source code, you cannot extend the functionality of this object. Also, notice that in this example all conversions just multiply the input with a conversion factor.
 Create a general superclass `UnitConversion` with one `apply` method and define **objects** `InchesToCentimeters`, `GallonsToLiters` and `MilesToKilometers` that extend `UnitConversion`. In the end, make sure that you can evaluate something like `InchesToCentimeters(20.5)` to `52.07`.
