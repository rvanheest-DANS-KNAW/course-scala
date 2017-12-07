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
package workshop6.solution

import java.nio.file.Path
import java.util.UUID

import better.files.File._
import better.files._

import scala.language.postfixOps

object BagStoreIndexerSolution extends App {

  case class BagFile(path: File, size: Long, sha1: String, content: String)
  case class Bag(uuid: UUID, path: File, files: Seq[BagFile])
  case class BagStore(path: File, name: String, bags: Seq[Bag])

  val stores = currentWorkingDirectory / "src" / "main" / "resources" / "workshop6" / "bagstores"
  val reportFile = currentWorkingDirectory / "src" / "main" / "resources" / "workshop6" / "bagstore-report-solution.txt"

  getBagStores(stores).map(generateReport).foreach { report =>
    reportFile.appendText(report).appendText("\n")
  }

  def getBagFiles(bag: File): Seq[BagFile] = {
    val files = for {
      file <- (bag / "data").listRecursively
      if file.isRegularFile
    } yield BagFile(file, file.size, file.sha1.toLowerCase, file.contentAsString)

    files.toSeq
  }

  def getBags(store: File): Seq[Bag] = {
    val bags = for {
      deposit <- store.walk(maxDepth = 2)
      relativeDeposit = store.relativize(deposit)
      if relativeDeposit.getNameCount == 2
      bag = deposit / "bag"
    } yield Bag(getUUID(relativeDeposit), bag, getBagFiles(bag))

    bags.toSeq
  }

  def getBagStores(baseDir: File): Seq[BagStore] = {
    stores.list.map(store => BagStore(store, store.name, getBags(store))).toSeq
  }

  def getUUID(path: Path): UUID = {
    UUID.fromString(formatUuidStrCanonically(path.toString.filterNot('/' ==)))
  }

  def formatUuidStrCanonically(s: String): String = {
    List(s.slice(0, 8), s.slice(8, 12), s.slice(12, 16), s.slice(16, 20), s.slice(20, 32)).mkString("-")
  }

  def generateReport(bagStore: BagStore): String = {
    s"""Bagstore '${bagStore.name}'
       |${bagStore.path}
       |${if (bagStore.bags.isEmpty) "  [empty]" else bagStore.bags.map(generateReport(_, bagStore.path)).mkString("\n")}
     """.stripMargin
  }

  def generateReport(bag: Bag, path: File): String = {
    s"""  Bag ${bag.uuid}
       |  ${path.parent.relativize(bag.path)}
       |${if (bag.files.isEmpty) "  [no files in data/]" else bag.files.map(generateReport(_, bag.path)).mkString("\n")}
     """.stripMargin
  }

  def generateReport(bagFile: BagFile, path: File): String = {
    s"    ${path.relativize(bagFile.path)} - ${bagFile.size} bytes - sha1: ${bagFile.sha1}"
  }
}
