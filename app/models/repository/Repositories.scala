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
package models.repository

import javax.inject.{Singleton, Inject}

import models.repository.admin.{ApiTokenRepository, TransactionTypeRepository}
import models.repository.brand._
import models.repository.event._
import play.api.Application

trait IRepositories {
  val address: AddressRepository
  val activity: ActivityRepository
  val apiToken: ApiTokenRepository
  val attendee: AttendeeRepository
  val brand: BrandRepository
  val brandBadge: BadgeRepository
  val brandCoordinator: BrandCoordinatorRepository
  val certificate: CertificateTemplateRepository
  val contribution: ContributionRepository
  val emailToken: EmailTokenRepository
  val evaluation: EvaluationRepository
  val event: EventRepository
  val eventInvoice: EventInvoiceRepository
  val eventCancellation: EventCancellationRepository
  val eventRequest: EventRequestRepository
  val eventType: EventTypeRepository
  val exchange: ExchangeRateRepository
  val experiment: ExperimentRepository
  val facilitator: FacilitatorRepository
  val fee: BrandFeeRepository
  val mailToken: PasswordTokenRepository
  val member: MemberRepository
  val license: LicenseRepository
  val org: OrganisationRepository
  val paymentRecord: PaymentRecordRepository
  val person: PersonRepository
  val product: ProductRepository
  val profileStrength: ProfileStrengthRepository
  val registeringUser: RegisteringUserRepository
  val socialProfile: SocialProfileRepository
  val userAccount: UserAccountRepository
  val identity: IdentityRepository
  val transactionType: TransactionTypeRepository
}

/** Contains references to all services so we can stub them in tests */
@Singleton
class Repositories @Inject()(val app: Application) extends IRepositories {

  lazy val address: AddressRepository = new AddressRepository(app)

  lazy val activity: ActivityRepository = new ActivityRepository(app)

  lazy val apiToken: ApiTokenRepository = new ApiTokenRepository(app)

  lazy val attendee: AttendeeRepository = new AttendeeRepository(app)

  lazy val brand: BrandRepository = new BrandRepository(app, this)

  lazy val brandBadge: BadgeRepository = new BadgeRepository(app)

  lazy val brandCoordinator: BrandCoordinatorRepository = new BrandCoordinatorRepository(app)

  lazy val certificate: CertificateTemplateRepository = new CertificateTemplateRepository(app)

  lazy val contribution: ContributionRepository = new ContributionRepository(app)

  lazy val emailToken: EmailTokenRepository = new EmailTokenRepository(app)

  lazy val evaluation: EvaluationRepository = new EvaluationRepository(app, this)

  lazy val event: EventRepository = new EventRepository(app, this)

  lazy val eventInvoice: EventInvoiceRepository = new EventInvoiceRepository(app)

  lazy val eventCancellation: EventCancellationRepository = new EventCancellationRepository(app)

  lazy val eventRequest: EventRequestRepository = new EventRequestRepository(app)

  lazy val eventType: EventTypeRepository = new EventTypeRepository(app)

  lazy val exchange: ExchangeRateRepository = new ExchangeRateRepository(app)

  lazy val experiment: ExperimentRepository = new ExperimentRepository(app)

  lazy val facilitator: FacilitatorRepository = new FacilitatorRepository(app)

  lazy val fee: BrandFeeRepository = new BrandFeeRepository(app)

  lazy val mailToken: PasswordTokenRepository = new PasswordTokenRepository(app)

  lazy val member: MemberRepository = new MemberRepository(app)

  lazy val license: LicenseRepository = new LicenseRepository(app)

  lazy val org: OrganisationRepository = new OrganisationRepository(app, this)

  lazy val paymentRecord: PaymentRecordRepository = new PaymentRecordRepository(app)

  lazy val person: PersonRepository = new PersonRepository(app, this)

  lazy val product: ProductRepository = new ProductRepository(app)

  lazy val profileStrength: ProfileStrengthRepository = new ProfileStrengthRepository(app)

  lazy val registeringUser: RegisteringUserRepository = new RegisteringUserRepository(app)

  lazy val socialProfile: SocialProfileRepository = new SocialProfileRepository(app)

  lazy val userAccount: UserAccountRepository = new UserAccountRepository(app)

  lazy val identity: IdentityRepository = new IdentityRepository(app)

  lazy val transactionType: TransactionTypeRepository = new TransactionTypeRepository(app)
}
