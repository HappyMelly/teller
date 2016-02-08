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
package models.service

import javax.inject.{Singleton, Inject}

import models.service.admin.{ApiTokenService, TransactionTypeService}
import models.service.brand._
import models.service.event._
import play.api.Application

trait IServices

/** Contains references to all services so we can stub them in tests */
@Singleton
class Services @Inject() (val app: Application) extends IServices {

  lazy val addressService: AddressService = new AddressService(app)

  lazy val activityService: ActivityService = new ActivityService(app)

  lazy val apiTokenService: ApiTokenService = new ApiTokenService(app)

  lazy val attendeeService: AttendeeService = new AttendeeService(app)

  lazy val brandService: BrandService = new BrandService(app, this)

  lazy val brandBadgeService: BadgeService = new BadgeService(app)

  lazy val brandCoordinatorService: BrandCoordinatorService = new BrandCoordinatorService(app)

  lazy val certificateService: CertificateTemplateService = new CertificateTemplateService(app)

  lazy val contributionService: ContributionService = new ContributionService(app)

  lazy val emailToken: EmailTokenService = new EmailTokenService(app)

  lazy val evaluationService: EvaluationService = new EvaluationService(app, this)

  lazy val eventService: EventService = new EventService(app, this)

  lazy val eventInvoiceService: EventInvoiceService = new EventInvoiceService(app)

  lazy val eventCancellationService: EventCancellationService = new EventCancellationService(app)

  lazy val eventRequestService: EventRequestService = new EventRequestService(app)

  lazy val eventTypeService: EventTypeService = new EventTypeService(app)

  lazy val exchangeService: ExchangeRateService = new ExchangeRateService(app)

  lazy val experimentService: ExperimentService = new ExperimentService(app)

  lazy val facilitatorService: FacilitatorService = new FacilitatorService(app)

  lazy val feeService: BrandFeeService = new BrandFeeService(app)

  lazy val mailTokenService: PasswordTokenService = new PasswordTokenService(app)

  lazy val memberService: MemberService = new MemberService(app)

  lazy val licenseService: LicenseService = new LicenseService(app)

  lazy val orgService: OrganisationService = new OrganisationService(app, this)

  lazy val paymentRecordService: PaymentRecordService = new PaymentRecordService(app)

  lazy val personService: PersonService = new PersonService(app, this)

  lazy val productService: ProductService = new ProductService(app)

  lazy val profileStrengthService: ProfileStrengthService = new ProfileStrengthService(app)

  lazy val registeringUserService: RegisteringUserService = new RegisteringUserService(app)

  lazy val socialProfileService: SocialProfileService = new SocialProfileService(app)

  lazy val userAccountService: UserAccountService = new UserAccountService(app)

  lazy val identityService: IdentityService = new IdentityService(app)

  lazy val transactionTypeService: TransactionTypeService = new TransactionTypeService(app)
}
