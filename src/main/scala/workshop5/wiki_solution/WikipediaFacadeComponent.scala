package workshop5.wiki_solution

import rx.lang.scala.Observable

import scala.io.Source

trait WikipediaFacadeComponent {

  val wikipediaFacade: WikipediaFacade

  trait WikipediaFacade {
    val baseUrl: String

    def search(word: String): Observable[String] = Observable.defer {
      val url = baseUrl + word.replace(" ", "%20")

      Observable.using(Source.fromURL(url))(r => Observable.just(r.mkString), _.close(), disposeEagerly = true)
    }
  }
}
