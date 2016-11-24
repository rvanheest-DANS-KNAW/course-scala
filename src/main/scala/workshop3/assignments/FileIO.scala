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
package workshop3.assignments

import java.io.{File, InputStream, Writer}

import resource.ManagedResource

case class Customer(id: String, firstName: String, lastName: String)
case class Product(id: String, title: String, price: Double)
case class Order(productId: String, customerId: String, amount: Int)

object FileIO {

  val customerFile = new File(getClass.getResource("/workshop3/customer.csv").toURI)
  val orderFile = new File(getClass.getResource("/workshop3/order.csv").toURI)
  val productFile = new File(getClass.getResource("/workshop3/product.csv").toURI)

  val report1File = new File("report1.txt")
  val report2File = new File("report2.txt")

  def readCustomers(in: InputStream): List[Customer] = ???

  def readProducts(in: InputStream): List[Product] = ???

  def readOrders(in: InputStream): List[Order] = ???

  def report1(orders: List[Order], products: List[Product], customers: List[Customer]): List[String] = ???

  def report2(orders: List[Order], products: List[Product], customers: List[Customer]): List[String] = ???

  def generateReports(customerInput: InputStream,
                      productInput: InputStream,
                      orderInput: InputStream): (List[String], List[String]) = ???

  def writeReport(report: List[String], output: Writer): Unit = ???

  val reports: ManagedResource[(List[String], List[String])] = ???

  val reportWriting: ManagedResource[Unit] = ???

  reportWriting.acquireAndGet(_ => println("done"))
}
