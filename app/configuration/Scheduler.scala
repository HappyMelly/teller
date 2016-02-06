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
package configuration

import java.util.concurrent.TimeUnit
import javax.inject.{Singleton, Inject}

import mail.reminder._
import models.Facilitator
import org.joda.time.{Seconds, LocalTime, LocalDate, LocalDateTime}
import play.libs.Akka
import play.api.Environment
import services.integrations.Email
import services.cleaners.ExpiredEventRequestCleaner

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

trait IScheduler

/**
  * Schedules a set of actions to run on the application start
  */
@Singleton
class Scheduler @Inject() (val env: Environment, val email: Email) extends IScheduler {
  start

  private def start = {
    if (sys.env.contains("DYNO") && sys.env("DYNO").equals("web.2")) {
      scheduleDailyAlerts
      scheduleMonthlyAlerts
    }
  }

  /**
    * Sends event confirmation alert in the beginning of each day
    */
  private def scheduleDailyAlerts = {
    val now = LocalDateTime.now()
    val targetDate = LocalDate.now.plusDays(1)
    val targetTime = targetDate.toLocalDateTime(new LocalTime(0, 0))
    val waitPeriod = Seconds.secondsBetween(now, targetTime).getSeconds * 1000
    Akka.system.scheduler.schedule(
      Duration.create(waitPeriod, TimeUnit.MILLISECONDS),
      Duration.create(24, TimeUnit.HOURS)) {
      (new EventReminder(email)).sendPostFactumConfirmation()
      (new EvaluationReminder(email)).sendToAttendees()
      Facilitator.updateFacilitatorExperience()
      ExpiredEventRequestCleaner.clean()
    }
  }

  /**
    * Sends event confirmation alert on the first day of each month
    */
  private def scheduleMonthlyAlerts = {
    val now = LocalDateTime.now()
    val targetDate = LocalDate.now.plusDays(1)
    val targetTime = targetDate.toLocalDateTime(new LocalTime(0, 0))
    val waitPeriod = Seconds.secondsBetween(now, targetTime).getSeconds * 1000
    Akka.system.scheduler.schedule(
      Duration.create(waitPeriod, TimeUnit.MILLISECONDS),
      Duration.create(24, TimeUnit.HOURS)) {
      if (now.getDayOfMonth == 1) {
        (new ProfileStrengthReminder(email)).sendToFacilitators()
        (new ExperimentReminder(email)).sendStatus()
        (new BrandReminder(email)).sendLicenseExpirationReminder()
        (new EventReminder(email)).sendUpcomingEventsNotification()
      }
    }
  }
}