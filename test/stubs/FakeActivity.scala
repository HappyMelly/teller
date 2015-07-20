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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package stubs

import models.BaseActivity

case class FakeActivity(id: Option[Long],
    subjectId: Long,
    subject: String,
    predicate: String,
    objectType: String,
    objectId: Long,
    activityObject: Option[String],
    supportiveObjectType: Option[String] = None,
    supportiveObjectId: Option[Long] = None,
    supportiveObject: Option[String] = None) extends BaseActivity {

  override def description: String = ""
  override def signedUp: FakeActivity = this
  override def created: FakeActivity = this
  override def updated: FakeActivity = this
  override def deleted: FakeActivity = this
  override def activated: FakeActivity = this
  override def deactivated: FakeActivity = this
  override def added: FakeActivity = this
  override def replaced: FakeActivity = this
  override def balanced: FakeActivity = this
  override def confirmed: FakeActivity = this
  override def approved: FakeActivity = this
  override def rejected: FakeActivity = this
  override def sent: FakeActivity = this
  override def connected: FakeActivity = this
  override def disconnected: FakeActivity = this
  override def uploadedSign: FakeActivity = this
  override def deletedSign: FakeActivity = this
  override def deletedImage: FakeActivity = this
  override def made: FakeActivity = this
  override def becameSupporter: FakeActivity = this

  override def insert(): FakeActivity = this
}