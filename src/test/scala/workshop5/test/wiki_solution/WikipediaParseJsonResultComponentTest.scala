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
package workshop5.test.wiki_solution

import org.scalatest.{ FlatSpec, Matchers }
import workshop5.wiki_solution.WikipediaParseJsonComponent

class WikipediaParseJsonResultComponentTest extends FlatSpec with Matchers with WikipediaParseJsonComponent {
  val responseParser: ResponseParser = new JsonResponseParser {}

  "parse" should "extract the titles of articles from the xml" in {
    val json =
      """
        |[
        |    "Hello world",
        |    [
        |        "\"Hello, World!\" program",
        |        "Hello World (Scandal album)",
        |    ],
        |    [
        |        "A \"Hello, World!\" program is a computer program that outputs or displays \"Hello, World!\" to the user. Being a very simple program in most programming languages, it is often used to illustrate the basic syntax of a programming language for a working program.",
        |        "Hello World is the sixth studio album by Japanese pop rock band, Scandal. The album was released on December 3, 2014 in Japan by Epic and being distributed in Europe through JPU Records.",
        |    ],
        |    [
        |        "https://en.wikipedia.org/wiki/%22Hello,_World!%22_program",
        |        "https://en.wikipedia.org/wiki/Hello_World_(Scandal_album)",
        |    ]
        |]
      """.stripMargin

    responseParser.parse(json) should {
      have size 2 and
        contain inOrderOnly("\"Hello, World!\" program", "Hello World (Scandal album)")
    }
  }

  it should "return an empty list if the xml contains no results" in {
    val json =
      """
        |[
        |    "   ",
        |    [],
        |    [],
        |    []
        |]
      """.stripMargin

    responseParser.parse(json) shouldBe empty
  }
}
