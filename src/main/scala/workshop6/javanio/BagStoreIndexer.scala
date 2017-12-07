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
package workshop6.javanio

import java.nio.file.{ Files, Path, Paths, StandardOpenOption }
import java.util.UUID

import org.apache.commons.codec.digest.DigestUtils
import resource._

import scala.collection.JavaConverters._
import scala.language.postfixOps

object BagStoreIndexer extends App {

  case class BagFile(path: Path, size: Long, sha1: String, content: String)
  case class Bag(uuid: UUID, path: Path, files: Seq[BagFile])
  case class BagStore(path: Path, name: String, bags: Seq[Bag])

  val stores = Paths.get("src/main/resources/workshop6/bagstores").toAbsolutePath
  val reportFile = Paths.get("bagstore-report.txt").toAbsolutePath

  getBagStores(stores).map(generateReport).foreach { report =>
    Files.write(reportFile, report.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE)
    Files.write(reportFile, "\n".getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE)
  }

  def getBagFiles(bag: Path): Seq[BagFile] = {
    for {
      file <- managed(Files.walk(bag.resolve("data"))).acquireAndGet(_.iterator().asScala.toList)
      if Files.isRegularFile(file)
      bytes = Files.readAllBytes(file)
    } yield BagFile(file, Files.size(file), new String(DigestUtils.sha1Hex(bytes)), new String(bytes))
  }

  def getBags(store: Path): Seq[Bag] = {
    for {
      deposit <- managed(Files.walk(store, 2)).acquireAndGet(_.iterator().asScala.toList)
      relativeDeposit = store.relativize(deposit)
      if relativeDeposit.getNameCount == 2
      bag = deposit.resolve("bag")
    } yield Bag(getUUID(relativeDeposit), bag, getBagFiles(bag))
  }

  def getBagStores(baseDir: Path): Seq[BagStore] = {
    managed(Files.list(baseDir)).acquireAndGet(_.iterator().asScala.toList)
      .map(store => BagStore(store, store.getFileName.toString, getBags(store)))
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

  def generateReport(bag: Bag, path: Path): String = {
    s"""  Bag ${bag.uuid}
       |  ${path.getParent.relativize(bag.path)}
       |${if (bag.files.isEmpty) "  [no files in data/]" else bag.files.map(generateReport(_, bag.path)).mkString("\n")}
     """.stripMargin
  }

  def generateReport(bagFile: BagFile, path: Path): String = {
    s"    ${path.relativize(bagFile.path)} - ${bagFile.size} bytes - sha1: ${bagFile.sha1}"
  }
}
