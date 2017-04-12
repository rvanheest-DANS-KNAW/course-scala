package workshop5.wiki_solution

import org.json4s.JsonAST.{ JArray, JString }
import org.json4s.native.JsonMethods

import scala.xml.XML

trait WikipediaParseComponent {

  val responseParser: ResponseParser

  trait ResponseParser {
    /**
     * Parse the input into a list of titles of wikipedia pages.
     *
     * @param input the raw response from Wikipedia's webservice API call
     * @return the list of related titles
     */
    def parse(input: String): Seq[String]
  }
}

trait WikipediaParseXmlComponent extends WikipediaParseComponent {

  trait XmlResponseParser extends ResponseParser {
    override def parse(xml: String): Seq[String] = {
      for {
        item <- XML.loadString(xml) \ "Section" \ "Item"
      } yield (item \ "Text").text
    }
  }
}

trait WikipediaParseJsonComponent extends WikipediaParseComponent {

  trait JsonResponseParser extends ResponseParser {
    override def parse(json: String): Seq[String] = {
      for {
        JArray(child) <- JsonMethods.parse(json)(1)
        JString(word) <- child
      } yield word
    }
  }
}
