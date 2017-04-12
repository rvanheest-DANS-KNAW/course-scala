package workshop5.wiki_solution

import rx.lang.scala.Observable

trait WikipediaSuggestionComponent {
  this: WikipediaFacadeComponent with WikipediaParseComponent =>

  val wikipediaSuggest: WikipediaSuggest

  trait WikipediaSuggest {
    def suggestArticles(word: String): Observable[String] = {
      wikipediaFacade.search(word)
        .map(responseParser.parse)
        .flatMapIterable(identity)
    }
  }
}
