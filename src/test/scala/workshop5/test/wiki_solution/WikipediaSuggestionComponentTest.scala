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

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FlatSpec, Matchers }
import rx.lang.scala.Observable
import rx.lang.scala.observers.TestSubscriber
import workshop5.wiki_solution.{ WikipediaFacadeComponent, WikipediaParseComponent, WikipediaSuggestionComponent }

class WikipediaSuggestionComponentTest extends FlatSpec with Matchers with MockFactory with WikipediaSuggestionComponent with WikipediaFacadeComponent with WikipediaParseComponent {
  val wikipediaFacade: WikipediaFacade = mock[WikipediaFacade]
  val responseParser: ResponseParser = mock[ResponseParser]
  val wikipediaSuggest: WikipediaSuggest = new WikipediaSuggest {}

  "suggestArticles" should "call the wikipedia API, interpret the response and return the result" in {
    wikipediaFacade.search _ expects "Hello World" returning Observable.just("some kind of result")
    responseParser.parse _ expects * returning Seq("foo", "bar", "baz", "qux")

    val testSubscriber = TestSubscriber[String]()
    wikipediaSuggest.suggestArticles("Hello World").subscribe(testSubscriber)

    testSubscriber.assertValues("foo", "bar", "baz", "qux")
    testSubscriber.assertNoErrors()
    testSubscriber.assertCompleted()
  }
}
