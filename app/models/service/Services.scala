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

import models.service.admin.ApiTokenService
import models.service.brand._
import models.service.event._

/** Contains references to all services so we can stub them in tests */
trait Services {

  def activityService: ActivityService = ActivityService.get

  def apiTokenService: ApiTokenService = ApiTokenService.get

  def brandService: BrandService = BrandService.get

  def brandBadgeService: BadgeService = BadgeService.get

  def brandCoordinatorService: BrandCoordinatorService = BrandCoordinatorService.get

  def certificateService: CertificateTemplateService = CertificateTemplateService.get

  def contributionService: ContributionService = ContributionService.get

  def emailToken: EmailTokenService = EmailTokenService.get

  def evaluationService: EvaluationService = EvaluationService.get

  def eventService: EventService = EventService.get

  def eventInvoiceService: EventInvoiceService = EventInvoiceService.get

  def eventCancellationService: EventCancellationService = EventCancellationService.get

  def eventRequestService: EventRequestService = EventRequestService.get

  def eventTypeService: EventTypeService = EventTypeService.get

  def experimentService: ExperimentService = ExperimentService.get

  def facilitatorService: FacilitatorService = FacilitatorService.get

  def feeService: BrandFeeService = BrandFeeService.get

  def mailTokenService: PasswordTokenService = PasswordTokenService.get

  def memberService: MemberService = MemberService.get

  def licenseService: LicenseService = LicenseService.get

  def orgService: OrganisationService = OrganisationService.get

  def participantService: ParticipantService = ParticipantService.get

  def paymentRecordService: PaymentRecordService = PaymentRecordService.get

  def personService: PersonService = PersonService.get

  def productService: ProductService = ProductService.get

  def profileStrengthService: ProfileStrengthService = ProfileStrengthService.get

  def registeringUserService: RegisteringUserService = RegisteringUserService.get

  def socialProfileService: SocialProfileService = SocialProfileService.get

  def userAccountService: UserAccountService = UserAccountService.get

  def identityService: IdentityService = IdentityService.get
}
