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
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import cron.SubscriptionUpdater
import cron.cleaners.{ExpiredEventRequestCleaner, TokenCleaner}
import cron.reminders._
import models.cm.Facilitator
import models.repository.Repositories
import org.joda.time.{LocalDate, LocalDateTime, LocalTime, Seconds}
import play.api.Environment
import services.integrations.Email

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

trait IScheduler

/**
  * Schedules a set of actions to run on the application start
  */
@Singleton
class Scheduler @Inject() (val env: Environment,
                           val email: Email,
                           val repos: Repositories,
                           val actors: ActorSystem) extends IScheduler {
  start

  private def start = {
    if (sys.env.contains("DYNO") && sys.env("DYNO").equals("web.2")) {
      scheduleDailyActions
      scheduleMonthlyActions
    }
  }

  private def runCleaners() = {
    (new ExpiredEventRequestCleaner(repos)).clean()
    (new TokenCleaner(repos).clean())
  }

  /**
    * Sends event confirmation alert in the beginning of each day
    */
  private def scheduleDailyActions = scheduler {
    println("INFO: Start of daily routines")

    val membership = new MembershipReminder(email, repos)
    membership.sendOneMonthExpirationReminder()
    val event = new EventReminder(email, repos)
    event.sendPostFactumConfirmation()
    val evaluation = new EvaluationReminder(email, repos)
    evaluation.sendToAttendees()
    Facilitator.updateFacilitatorExperience(repos)
    val subscription = new SubscriptionUpdater(repos)
    subscription.update()
    runCleaners()

    println("INFO: End of daily routines")
  }

  /**
    * Sends event confirmation alert on the first day of each month
    */
  private def scheduleMonthlyActions = scheduler {
    if (LocalDateTime.now().getDayOfMonth == 1) {
      println("INFO: Start of monthly routines")

      (new ProfileStrengthReminder(email, repos)).sendToFacilitators()
      (new BrandReminder(email, repos)).sendLicenseExpirationReminder()

      println("INFO: End of monthly routines")
    }
  }

  private def scheduler[A](f: => A) = {
    val now = LocalDateTime.now()
    val targetDate = LocalDate.now.plusDays(1)
    val targetTime = targetDate.toLocalDateTime(new LocalTime(0, 0))
    val waitPeriod = Seconds.secondsBetween(now, targetTime).getSeconds * 1000
    val initialDelay = Duration.create(waitPeriod, TimeUnit.MILLISECONDS)
    val interval = Duration.create(24, TimeUnit.HOURS)
    actors.scheduler.schedule(initialDelay, interval) {
      f
    }
  }
}