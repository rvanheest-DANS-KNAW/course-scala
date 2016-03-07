// translated from https://github.com/frode-carlsen/scala-workshop/blob/master/scala-workshop/src/main/scala/oppgave2/SultneDyr.scala

type Animal = String
type Sound = String
type Food = String

/*
 * Example for constructor parameters:
 * val animalSays = Map("Dog" -> "Woof", "Cat" -> "Meow", "Cow" -> "Mooh")
 * val asksForFood = Map("Woof" -> "Meat", "Meow" -> "Fish", "Mooh" -> "Grass")
 *
 * HINT!!! Follow the type signature for Map.get and Option (especially .map and .flatMap)
 *
 * Map[A, B]
 *  def get(a: A): Option[B]
 *
 * Option[A]
 *  def flatMap[B](f: A => Option[B]): Option[B]
 *  def map[B](f: A => B): Option[B]
 *
 */
class Animals(animalSays: Map[Animal, Sound], asksForFood: Map[Sound, Food]) {

  // returns an Option of the sound an animal makes
  def whatSays(animal: Animal): Option[Sound] = animalSays.get(animal)

  // returns an Option of the sound (in CAPITAL) an animal makes
  def WHAT_SAYS(animal: Animal): Option[Sound] = whatSays(animal).map(_.toUpperCase)

  // returns an Option of the food an animal eats
  def whatEats(animal: Animal): Option[Food] = whatSays(animal).flatMap(asksForFood.get)

  // returns a List of "<animal> eats <food>" based on what each animal likes to eat.
  def whatEat(animals: List[Animal]): List[String] = {
    animals.flatMap(animal => whatEats(animal).map(food => s"$animal eats $food"))
  }

  // returns an Option of the food (in CAPITAL) an animal eats
  def WHAT_EATS(animal: Animal): Option[Food] = whatEats(animal).map(_.toUpperCase)
}
