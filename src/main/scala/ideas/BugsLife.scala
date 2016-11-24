package ideas

import javafx.application.Application
import javafx.geometry.{Bounds, Pos}
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.StackPane
import javafx.scene.media.AudioClip
import javafx.scene.paint.Color
import javafx.stage.{Stage, WindowEvent}

import rx.lang.scala.JavaConverters._
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject
import rx.observables.JavaFxObservable
import rx.schedulers.JavaFxScheduler

import scala.concurrent.duration._
import scala.language.postfixOps

object Utils {
  def getResource(s: String): String = this.getClass.getResource(s).toString
}

class Sky(screenWidth: Int, screenHeight: Int) extends Canvas(screenWidth, screenHeight) {
  val context = getGraphicsContext2D
  context.setFill(Color.AZURE)
  context.fillRect(0, 0, screenWidth, screenHeight)
}

class Grass(screenWidth: Int) extends ImageView {
  val tile = new Image(Utils.getResource("/ideas/bugslife/GrassBlock.png"))
  val height = tile.getHeight
  val tilesCount = math.ceil(screenWidth / tile.getWidth).toInt + 1

  val tiles = (0 until tilesCount).map(i => new ImageView(tile) {
    setTranslateX(i * getImage.getWidth)
  }).toList
}

class Sun(screenHeight: Int) extends ImageView {

  def showHeart(showHeart: Boolean): Unit = {
    val image =
      if (showHeart) new Image(Utils.getResource("/ideas/bugslife/Heart.png"))
      else new Image(Utils.getResource("/ideas/bugslife/Star.png"))

    setImage(image)
  }

  showHeart(false)
  setTranslateY(-(screenHeight - 200))
}

class Bug(screenHeight: Int, grassHeight: Double) extends ImageView(new Image(Utils.getResource("/ideas/bugslife/EnemyBug.png"))) {
  val homeY = (-grassHeight / 2) - 5
  val gravity = 0.1

  setTranslateY(homeY)
  setTranslateX(screenHeight / 2)
}

class BugsLife extends Application {

  def start(stage: Stage) = {
    val screenWidth = 800
    val screenHeight = 600

    //  ^
    //  |
    // -dy
    //  |
    // (0,0) -----dx--->

    val root = new StackPane()
    root.setAlignment(Pos.BOTTOM_LEFT)
    val scene = new Scene(root)

    val sky = new Sky(screenWidth, screenHeight)
    root.getChildren.add(sky)

    val grass = new Grass(screenWidth)
    root.getChildren.addAll(grass.tiles :_*)

    val sun = new Sun(screenHeight)
    root.getChildren.add(sun)

    val bug = new Bug(screenHeight, grass.height)
    root.getChildren.add(bug)

    val clock = Observable.interval(initialDelay = 0 seconds, period = (1/60.0) second)
      .observeOn(JavaFxScheduler.getInstance().asScala)

    val grassMoves = clock.map(_ => 1)
      .subscribe(dX =>
        grass.tiles.foreach(tile => {
          val translateX =
            if (tile.getTranslateX <= -tile.getImage.getWidth) screenWidth - dX
            else tile.getTranslateX - dX

          tile.setTranslateX(translateX)
        }))

    val sunMoves = clock.map(_ => 3)
      .subscribe(dX => {
        val translateX =
          if (sun.getTranslateX <= -sun.getImage.getWidth)
            screenWidth - dX
          else
            sun.getTranslateX - dX

        sun.setTranslateX(translateX)
      })

    val jumps = PublishSubject[Double]()

    val bugMoves = jumps
      .flatMap(v0 => clock.scan(v0)((vi, _) => vi - bug.gravity).takeUntil(jumps))
      .subscribe(dy => {
        val translateY =
          if (bug.getTranslateY < bug.homeY + dy) bug.getTranslateY - dy
          else bug.homeY

        bug.setTranslateY(translateY)
      })

    val jumpSpeed = 8
    JavaFxObservable.fromSceneEvents(scene, KeyEvent.KEY_PRESSED)
      .asScala
      .filter(keyEvent => keyEvent.getCode == KeyCode.SPACE)
      .filter(_ => bug.getTranslateY >= bug.homeY)
      .subscribe(_ => {
        new AudioClip(Utils.getResource("/ideas/bugslife/smb3_jump.wav")).play()
        jumps.onNext(jumpSpeed)
      })

    val heartBoundingBoxes: Observable[Bounds] = clock.map(_ => sun.localToScene(sun.getLayoutBounds))
    val bugBoundingBoxes: Observable[Bounds] = clock.map(_ => bug.localToScene(bug.getLayoutBounds))

    val collision = bugBoundingBoxes.combineLatestWith(heartBoundingBoxes)(_.intersects(_))
      .slidingBuffer(2, 1)
      .filter { case Seq(prev, curr) => prev != curr }
      .map { case Seq(prev, _) => prev }
      .subscribe(prev => {
        sun.showHeart(!prev)

        if (!prev) new AudioClip(Utils.getResource("/ideas/bugslife/smb3_coin.wav")).play()
      })

    JavaFxObservable.fromWindowEvents(stage, WindowEvent.WINDOW_SHOWN)
      .asScala
      .subscribe(_ => new AudioClip(Utils.getResource("/ideas/bugslife/smb3_power-up.wav")).play())

    JavaFxObservable.fromWindowEvents(stage, WindowEvent.WINDOW_HIDDEN)
      .asScala
      .subscribe(_ => {
        grassMoves.unsubscribe()
        sunMoves.unsubscribe()
        bugMoves.unsubscribe()
        collision.unsubscribe()
      })

    stage.setScene(scene)
    stage.setTitle("A Bugs Life")
    stage.show()
  }
}

object BugsLife extends App {
  Application.launch(classOf[BugsLife])
}
