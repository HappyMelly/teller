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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package modules

import com.google.inject.AbstractModule
import models.actors._
import models.cm.evaluation.Mailer
import models.cm.facilitator.MailChimpSubscriber
import play.api.libs.concurrent.AkkaGuiceSupport
import services.integrations.EmailActor

/**
  * Initialises all actors
  */
class Actors extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[EmailActor]("email")
    bindActor[Mailer]("evaluation-mailer")
    bindActor[EventRatingCalculator]("event-rating")
    bindActor[FacilitatorRatingCalculator]("facilitator-rating")
    bindActor[NotificationDispatcher]("notification")
    bindActor[PeerCreditsConfigurator]("peer-credits")
    bindActor[ProfileStrengthRecalculator]("profile-strength")
    bindActor[MailChimpSubscriber]("mailchimp-subscriber")
  }
}

