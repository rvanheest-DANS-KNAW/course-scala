/**
 * Copyright (C) 2016 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
