package workshop5.wiki_solution

object Main extends App
  with WikipediaSuggestionComponent
  with WikipediaFacadeComponent
  with WikipediaParseXmlComponent {

  val wikipediaFacade: WikipediaFacade = new WikipediaFacade {
    val baseUrl: String = "https://en.wikipedia.org/w/api.php?action=opensearch&format=xml&search="
  }
  val responseParser: ResponseParser = new XmlResponseParser {}
  val wikipediaSuggest: WikipediaSuggest = new WikipediaSuggest {}

  wikipediaSuggest.suggestArticles("Hello World")
    .subscribe(println, _.printStackTrace())
}
