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

import javax.inject.{Inject, Singleton}

import models.repository.admin.{CommercialCustomerRepository, TransactionTypeRepository}
import models.repository.cm.brand._
import play.api.Application

trait IRepositories {
  val core: models.repository.core.Repositories
  val cm: models.repository.cm.Repositories

  val address: AddressRepository
  val activity: ActivityRepository
  val commercialCustomers: CommercialCustomerRepository
  val contribution: ContributionRepository
  val emailToken: EmailTokenRepository
  val exchange: ExchangeRateRepository
  val experiment: ExperimentRepository
  val fee: BrandFeeRepository
  val mailToken: PasswordTokenRepository
  val member: MemberRepository
  val org: OrganisationRepository
  val notification: NotificationRepository
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

  lazy val cm: models.repository.cm.Repositories = new models.repository.cm.Repositories(app, this)

  lazy val core: models.repository.core.Repositories = new models.repository.core.Repositories(app)

  lazy val address: AddressRepository = new AddressRepository(app)

  lazy val activity: ActivityRepository = new ActivityRepository(app)

  lazy val commercialCustomers: CommercialCustomerRepository = new CommercialCustomerRepository(app)

  lazy val contribution: ContributionRepository = new ContributionRepository(app)

  lazy val emailToken: EmailTokenRepository = new EmailTokenRepository(app)

  lazy val exchange: ExchangeRateRepository = new ExchangeRateRepository(app)

  lazy val experiment: ExperimentRepository = new ExperimentRepository(app)

  lazy val fee: BrandFeeRepository = new BrandFeeRepository(app)

  lazy val mailToken: PasswordTokenRepository = new PasswordTokenRepository(app)

  lazy val member: MemberRepository = new MemberRepository(app)

  lazy val notification: NotificationRepository = new NotificationRepository(app)

  lazy val org: OrganisationRepository = new OrganisationRepository(app, this)

  lazy val person: PersonRepository = new PersonRepository(app, this)

  lazy val product: ProductRepository = new ProductRepository(app)

  lazy val profileStrength: ProfileStrengthRepository = new ProfileStrengthRepository(app)

  lazy val registeringUser: RegisteringUserRepository = new RegisteringUserRepository(app)

  lazy val socialProfile: SocialProfileRepository = new SocialProfileRepository(app)

  lazy val userAccount: UserAccountRepository = new UserAccountRepository(app)

  lazy val identity: IdentityRepository = new IdentityRepository(app)

  lazy val transactionType: TransactionTypeRepository = new TransactionTypeRepository(app)
}
