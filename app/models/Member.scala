/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
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
package models

import org.joda.money.Money
import org.joda.time.{DateTime, LocalDate}

case class Member(
    id: Option[Long],
    objectId: Long,
    person: Boolean,
    funder: Boolean,
    fee: Money,
    renewal: Boolean = true,
    since: LocalDate,
    until: LocalDate,
    existingObject: Boolean,
    reason: Option[String] = None,
    created: DateTime,
    createdBy: Long,
    updated: DateTime,
    updatedBy: Long) extends ActivityRecorder {

  private var _memberObj: (Option[Person], Option[Organisation]) = (None, None)

  /** Returns true if membership starts before current date and ends after */
  lazy val active: Boolean = {
    val now = LocalDate.now()
    (since.isBefore(now) || since.isEqual(now)) && (until.isAfter(now) || until.isEqual(now))
  }

  def countryCode: String = if (person && _memberObj._1.nonEmpty)
    _memberObj._1.get.address.countryCode
  else if (!person && _memberObj._2.nonEmpty)
    _memberObj._2.get.countryCode
  else ""

  def city: Option[String] = if (person && _memberObj._1.nonEmpty)
    _memberObj._1.get.address.city
  else if (!person && _memberObj._2.nonEmpty)
    _memberObj._2.get.city
  else None

  /**
   * Returns a link to public profile on Happy Melly website
   */
  def profileUrl: String = "http://happymelly.com/members#%s".format(id.getOrElse(0))

  /**
   * Returns identifier of the object
   */
  def identifier: Long = id.getOrElse(0)

  /**
   * Returns string identifier which can be understood by human
   *
   * For example, for object 'Person' human identifier is "[FirstName] [LastName]"
   */
  def humanIdentifier: String = name

  /**
   * Returns type of this object
   */
  def objectType: String = Activity.Type.Member

  def memberObj_=(person: Person) = {
    _memberObj = (Some(person), None)
  }

  def memberObj_=(org: Organisation) = {
    _memberObj = (None, Some(org))
  }

  def memberObj: (Option[Person], Option[Organisation]) = _memberObj

  /** Returns name of this member depending of its type */
  def name: String = if (person && _memberObj._1.nonEmpty)
    _memberObj._1.get.fullName
  else if (!person && _memberObj._2.nonEmpty)
    _memberObj._2.get.name
  else ""

  /** Returns a link to avatar/logo of this member depending on its type */
  def image: Option[String] = if (person && _memberObj._1.nonEmpty)
    _memberObj._1.get.photo.url
  else
    None
}