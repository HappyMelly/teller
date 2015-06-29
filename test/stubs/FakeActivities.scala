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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package stubs

import controllers.Activities
import models.{ BaseActivity, Activity, ActivityRecorder, Person }

trait FakeActivities extends Activities {

  /**
   * Returns activity object with data from the given object
   *
   * @param obj Activity related object
   * @param subject Person who does an activity
   * @param supportiveObj Optional supportive object
   */
  override def activity(obj: ActivityRecorder,
    subject: Person,
    supportiveObj: Option[ActivityRecorder] = None): BaseActivity = {
    supportiveObj map { supportive ⇒
      new FakeActivity(None,
        subject.id.get,
        subject.fullName,
        Activity.Predicate.None,
        obj.objectType,
        obj.identifier,
        Some(obj.humanIdentifier),
        Some(supportive.objectType),
        Some(supportive.identifier),
        Some(supportive.humanIdentifier))
    } getOrElse {
      new FakeActivity(None,
        subject.id.get,
        subject.fullName,
        Activity.Predicate.None,
        obj.objectType,
        obj.identifier,
        Some(obj.humanIdentifier))
    }
  }
}