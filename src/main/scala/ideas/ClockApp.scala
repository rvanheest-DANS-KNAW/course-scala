package ideas

import java.time.LocalDateTime
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.effect.Glow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, Line}
import javafx.scene.transform.Rotate
import javafx.scene.{Group, Scene}
import javafx.stage.Stage

import rx.lang.scala.JavaConverters._
import rx.lang.scala.Observable
import rx.observables.JavaFxObservable
import rx.schedulers.JavaFxScheduler

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

// taken from https://gist.github.com/jewelsea/2658491 and ported to Scala/Rx
class ClockApp extends Application {
  def start(stage: Stage) = {

    // analogue clock
    val face = new Circle(100, 100, 100)
    face.setId("face")

    val brand = new Label("DANS/KNAW")
    brand.setId("brand")

    // position of the brand text
    JavaFxObservable.fromObservableValue(face.centerXProperty()).asScala
      .combineLatest(JavaFxObservable.fromObservableValue(brand.widthProperty()).asScala)
      .map { case (fCenter, bWidth) => fCenter.doubleValue() - (bWidth.doubleValue() / 2) }
      .subscribe(lx => brand.setLayoutX(lx))
    JavaFxObservable.fromObservableValue(face.centerYProperty()).asScala
      .combineLatest(JavaFxObservable.fromObservableValue(face.radiusProperty()).asScala)
      .map { case (fCenter, fRadius) => fCenter.doubleValue() + (fRadius.doubleValue() / 2) }
      .subscribe(ly => brand.setLayoutY(ly))

    val hourHand = new Line(0, 0, 0, -50)
    hourHand.setId("hourHand")
    hourHand.setTranslateX(100)
    hourHand.setTranslateY(100)

    val minuteHand = new Line(0, 0, 0, -75)
    minuteHand.setId("minuteHand")
    minuteHand.setTranslateX(100)
    minuteHand.setTranslateY(100)

    val secondHand = new Line(0, 15, 0, -88)
    secondHand.setId("secondHand")
    secondHand.setTranslateX(100)
    secondHand.setTranslateY(100)

    val spindle = new Circle(100, 100, 5)
    spindle.setId("spindle")

    val ticks = new Group()
    (0 until 12).map(i => {
      val tick = new Line(0, -83, 0, -93)
      tick.getStyleClass.add("tick")
      tick.setTranslateX(100)
      tick.setTranslateY(100)
      tick.getTransforms.add(new Rotate(i * 360 / 12))
      tick
    }).foreach(ticks.getChildren.add)

    val analogueClock = new Group(face, brand, ticks, spindle, hourHand, minuteHand, secondHand)

    // digital clock
    val digitalClock = new Label
    digitalClock.setId("digitalClock")

    // animate handles
    Observable.interval(0 seconds, 1 second)
      .map(_ => {
        val dateTime = LocalDateTime.now
        val secondDegrees = dateTime.getSecond * 360 / 60
        val minuteDegrees = (dateTime.getMinute + secondDegrees / 360.0) * 360 / 60
        val hourDegrees = (dateTime.getHour + minuteDegrees / 360.0) * 360 / 12
        (new Rotate(secondDegrees), new Rotate(minuteDegrees), new Rotate(hourDegrees))
      })
      .observeOn(JavaFxScheduler.getInstance().asScala)
      .subscribe(rotations => {
        val (seconds, minutes, hours) = rotations

        def change(hand: Line, rotate: Rotate) = {
          hand.getTransforms.clear()
          hand.getTransforms.add(rotate)
        }

        change(secondHand, seconds)
        change(minuteHand, minutes)
        change(hourHand, hours)
      })

    // digital clock updates its time every second
    Observable.interval(1 second)
      .map(_ => {
        def pad(fieldWith: Int, padChar: Char, s: String): String = {
          padChar.toString * (fieldWith - s.length) + s
        }

        val dt = LocalDateTime.now
        val hour = pad(2, '0', dt.getHour.toString)
        val minute = pad(2, '0', dt.getMinute.toString)
        val second = pad(2, '0', dt.getSecond.toString)

        s"$hour:$minute:$second"
      })
      .observeOn(JavaFxScheduler.getInstance().asScala)
      .subscribe(time => digitalClock.setText(time))

    // glow effect when hovering over the analogue clock
    val glow = new Glow
    JavaFxObservable.fromNodeEvents(analogueClock, MouseEvent.MOUSE_ENTERED)
      .asScala
      .foreach(_ => analogueClock.setEffect(glow))
    JavaFxObservable.fromNodeEvents(analogueClock, MouseEvent.MOUSE_EXITED)
      .asScala
      .foreach(_ => analogueClock.setEffect(null))


    val layout = new VBox(analogueClock, digitalClock)
    layout.setAlignment(Pos.CENTER)

    val scene = new Scene(layout, Color.TRANSPARENT)
    scene.getStylesheets.add(getClass.getResource("/ideas/clockapp.css").toExternalForm)

    stage.setScene(scene)
    stage.show()
  }
}

object ClockApp extends App {
  Application.launch(classOf[ClockApp])
}
