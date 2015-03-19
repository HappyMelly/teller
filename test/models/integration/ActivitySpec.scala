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

package models.integration

import integration.PlayAppSpec
import models.Activity

class ActivitySpec extends PlayAppSpec {

  "Activity" should {
    "generate good description without supportive object" in {
      val activity = new Activity(None, 1L, "Bill", Activity.Predicate.Created,
        "brand", 2L, Some("Test"))
      activity.description must_== "Bill (id = 1) added brand (id = 2) Test"
      activity.toString must_== "Added brand Test"
      val activity2 = new Activity(None, 3L, "Teresa", Activity.Predicate.Created,
        "brand", 5L, Some("Bumper"))
      activity2.description must_== "Teresa (id = 3) added brand (id = 5) Bumper"
      activity2.toString must_== "Added brand Bumper"
    }
    "generate good description with supportive object" in {
      val activity = new Activity(None, 1L, "Bill", Activity.Predicate.Connected,
        "brand", 2L, Some("Test"), Some(Activity.Type.Org), Some(2L), Some("Two"))
      activity.description must_== "Bill (id = 1) connected brand (id = 2) Test to organisation (id = 2) Two"
      activity.toString must_== "Connected brand Test to organisation Two"
      val activity2 = new Activity(None, 3L, "Teresa", Activity.Predicate.Connected,
        "brand", 5L, Some("Bumper"), Some(Activity.Type.Person), Some(2L),
        Some("Ivan Pupkin"))
      activity2.description must_== "Teresa (id = 3) connected brand (id = 5) Bumper to person (id = 2) Ivan Pupkin"
      activity2.toString must_== "Connected brand Bumper to person Ivan Pupkin"
    }
  }
}
