/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2016, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package libs.mailchimp

import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.{WSCookie, WSResponse}

import scala.xml.Elem
import scala.xml.factory.XMLLoader

/**
  * Represents fake http response
  */
case class FakeResponse(status: Int, body: String) extends WSResponse with XMLLoader[Elem] {

  def allHeaders: Map[String, Seq[String]] = Map()

  def underlying[T]: T = 1.asInstanceOf[T]

  def statusText: String = ""

  def header(key: String): Option[String] = None

  def cookies: Seq[WSCookie] = Seq()

  def cookie(name: String): Option[WSCookie] = None

  lazy val xml: Elem = loadString(body)

  lazy val json: JsValue = Json.parse(body)

  def bodyAsBytes: Array[Byte] = Array[Byte]()

}