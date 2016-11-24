package ideas

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{GridPane, VBox}
import javafx.scene.paint.Color
import javafx.stage.Stage

import org.apache.commons.lang3.StringUtils
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods
import rx.lang.scala.JavaConverters._
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ComputationScheduler
import rx.observables.JavaFxObservable
import rx.schedulers.JavaFxScheduler

import scala.io.Source
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

case class Position(lat: Double, lon: Double, place: String)
case class General(main: String, icon: String)
case class Wind(speed: Double, direction: Int)
case class Conditions(temperature: Double, pressure: Int, wind: Wind, clouds: Int)
case class Weather(position: Position, general: General, conditions: Conditions)

class OpenWeatherMapService {

  val appID = "ce871b5c3e592a568fb6c7501a3d3bc8"
  implicit val formats = DefaultFormats

  def from(url: String): Observable[String] = {
    Observable.using(Source.fromURL(url))(r => Observable.just(r.mkString), _.close(), disposeEagerly = true)
  }

  def getWeatherByCity(city: String): Observable[String] = {
    from(s"http://api.openweathermap.org/data/2.5/weather?q=$city&APPID=$appID&units=metric")
  }

  def getWeatherFromJson(json: String): Try[Weather] = Try {
    val total = JsonMethods.parse(json)

    val position = {
      val coord = total \ "coord"
      val lat = (coord \ "lat").extract[Double]
      val lon = (coord \ "lon").extract[Double]
      val name = (total \ "name").extract[String]
      val country = (total \ "sys" \ "country").extract[String]
      Position(lat, lon, s"$name, $country")
    }

    val general = {
      val weather = total \ "weather" apply 0
      val main = (weather \ "main").extract[String]
      val icon = (weather \ "icon").extract[String]
      General(main, icon)
    }

    val wind = {
      val wind = total \ "wind"
      val speed = (wind \ "speed").extract[Double]
      val direction = (wind \ "deg").extract[Int]
      Wind(speed, direction)
    }

    val conditions = {
      val main = total \ "main"
      val temp = (main \ "temp").extract[Double]
      val pressure = (main \ "pressure").extract[Int]
      val clouds = (total \ "clouds" \ "all").extract[Int]
      Conditions(temp, pressure, wind, clouds)
    }

    Weather(position, general, conditions)
  }
}

class CurrentWeather extends Application {

  type WeatherChart = GridPane
  val service1 = new OpenWeatherMapService

  def showEmptyWeatherChart: WeatherChart = {
    val gridPane = new GridPane()
    gridPane.setPadding(new Insets(10))
    gridPane.setHgap(30)
    gridPane.setVgap(8)

    gridPane.add(new Label("Place"), 0, 0)
    gridPane.add(new Label("(lat, lon)"), 0, 1)
    gridPane.add(new Label("Weather"), 0, 2)
    gridPane.add(new Label("Temperature"), 0, 3)
    gridPane.add(new Label("Pressure"), 0, 4)
    gridPane.add(new Label("Wind"), 0, 5)
    gridPane.add(new Label("Clouds"), 0, 6)

    gridPane
  }

  def showWeatherConditions(weather: Weather): WeatherChart = {
    val gridPane = showEmptyWeatherChart

    val img = new Image(s"http://openweathermap.org/img/w/${weather.general.icon}.png")
    val imgLabel = new Label(weather.general.main, new ImageView(img))
    imgLabel.setContentDisplay(ContentDisplay.RIGHT)

    gridPane.add(new Label(weather.position.place), 1, 0)
    gridPane.add(new Label(s"(${weather.position.lat}, ${weather.position.lon})"), 1, 1)
    gridPane.add(imgLabel, 1, 2)
    gridPane.add(new Label(s"${weather.conditions.temperature}°C"), 1, 3)
    gridPane.add(new Label(s"${weather.conditions.pressure} hPa"), 1, 4)
    gridPane.add(new Label(s"${weather.conditions.wind.speed} meter/sec in ${weather.conditions.wind.direction} degrees"), 1, 5)
    gridPane.add(new Label(s"${weather.conditions.clouds}%"), 1, 6)

    gridPane
  }

  def start(stage: Stage) = {
    val label = new Label("City:")
    val place = new TextField
    place.setPromptText("search...")
    val searchButton = new Button("search")
    searchButton.setMaxWidth(Double.MaxValue)

    val searchBox = new VBox(8, label, place, searchButton)
    searchBox.setPadding(new Insets(10))

    val error = new Label()
    error.setTextFill(Color.RED)

    val vbox = new VBox(12, searchBox, showEmptyWeatherChart, error)
    vbox.setPadding(new Insets(10))

    def setWeatherChart(weatherChart: WeatherChart): Unit = {
      vbox.getChildren.set(1, weatherChart)
    }

    JavaFxObservable.fromNodeEvents(searchButton, ActionEvent.ACTION).asScala
      .withLatestFrom(JavaFxObservable.fromObservableValue(place.textProperty()).asScala)((_, text) => text)
      .merge(JavaFxObservable.fromNodeEvents(place, ActionEvent.ACTION).asScala.map(_ => place.getText))
      .observeOn(ComputationScheduler())
      .filter(StringUtils.isNotBlank)
      .map(_.replace(" ", ""))
      .flatMap(service1.getWeatherByCity)
      .flatMap(service1.getWeatherFromJson(_) match {
        case Success(weather) => Observable.just(weather)
        case Failure(e) => Observable.error(e)
      })
      .observeOn(JavaFxScheduler.getInstance().asScala)
      .doOnNext(_ => error.setText(""))
      .doOnError(e => {
        error.setText(s"Something went wrong: ${e.getMessage}")
        setWeatherChart(showEmptyWeatherChart)
      })
      .retry
      .map(showWeatherConditions)
      .subscribe(chart => setWeatherChart(chart))

    stage.setScene(new Scene(vbox, 350, 400))
    stage.setTitle("Current weather")
    stage.show()
  }
}

object CurrentWeather extends App {
  Application.launch(classOf[CurrentWeather])
}
