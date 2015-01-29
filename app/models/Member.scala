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

import models.service.Services
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }

case class Member(
  id: Option[Long],
  objectId: Long,
  person: Boolean,
  funder: Boolean,
  fee: Money,
  since: LocalDate,
  existingObject: Boolean,
  created: DateTime,
  createdBy: Long,
  updated: DateTime,
  updatedBy: Long) extends Services {

  private var _memberObj: (Option[Person], Option[Organisation]) = (None, None)

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

  /** Records this member to database */
  def insert: Member = memberService.insert(this)

  /** Updates this member in database */
  def update: Member = memberService.update(this)
}