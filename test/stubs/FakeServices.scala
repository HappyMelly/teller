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
 * or in writing Happy Melly One, Handelsplein 37, Rotterdam,
 * The Netherlands, 3071 PR
 */
package stubs

import models.service._
import models.service.admin.ApiTokenService
import models.service.brand._
import models.service.event.EventCancellationService

trait FakeServices extends Services {

  private var _activityService = new ActivityService
  private var _apiTokenService = new ApiTokenService
  private var _brandService = new BrandService
  private var _brandTeamMemberService = new BrandCoordinatorService
  private var _certificateService = new CertificateTemplateService
  private var _contributionService = new ContributionService
  private var _evaluationService = new EvaluationService
  private var _eventService = new EventService
  private var _eventCancellationService = new EventCancellationService
  private var _eventTypeService = new EventTypeService
  private var _experimentService = new ExperimentService
  private var _facilitatorService = new FacilitatorService
  private var _feeService = new BrandFeeService
  private var _licenseService = new LicenseService
  private var _memberService = new MemberService
  private var _orgService = new OrganisationService
  private var _paymentRecordService = new PaymentRecordService
  private var _personService = new PersonService
  private var _productService = new ProductService
  private var _profileStrengthService = new ProfileStrengthService
  private var _socialProfileService = new SocialProfileService
  private var _translationService = new TranslationService
  private var _userAccountService = new UserAccountService

  def activityService_=(service: ActivityService) = _activityService = service
  override def activityService: ActivityService = _activityService

  def apiTokenService_=(service: ApiTokenService) = _apiTokenService = service
  override def apiTokenService: ApiTokenService = _apiTokenService

  def brandService_=(service: BrandService) = _brandService = service
  override def brandService: BrandService = _brandService

  def brandCoordinatorService_=(service: BrandCoordinatorService) =
    _brandTeamMemberService = service
  override def brandCoordinatorService = _brandTeamMemberService

  def certificateService_=(service: CertificateTemplateService) = _certificateService = service
  override def certificateService: CertificateTemplateService = _certificateService

  def contributionService_=(service: ContributionService) = _contributionService = service
  override def contributionService: ContributionService = _contributionService

  def evaluationService_=(service: EvaluationService) = _evaluationService = service
  override def evaluationService: EvaluationService = _evaluationService

  def eventService_=(service: EventService) = _eventService = service
  override def eventService: EventService = _eventService

  def eventCancellationService_=(service: EventCancellationService) =
    _eventCancellationService = service
  override def eventCancellationService: EventCancellationService = _eventCancellationService

  def eventTypeService_=(service: EventTypeService) = _eventTypeService = service
  override def eventTypeService: EventTypeService = _eventTypeService

  def experimentService_=(service: ExperimentService) = _experimentService = service
  override def experimentService: ExperimentService = _experimentService

  def facilitatorService_=(service: FacilitatorService) = _facilitatorService = service
  override def facilitatorService: FacilitatorService = _facilitatorService

  def feeService_=(service: BrandFeeService) = _feeService = service
  override def feeService: BrandFeeService = _feeService

  def licenseService_=(service: LicenseService) = _licenseService = service
  override def licenseService: LicenseService = _licenseService

  def memberService_=(service: MemberService) = _memberService = service
  override def memberService: MemberService = _memberService

  def paymentRecordService_=(service: PaymentRecordService) = {
    _paymentRecordService = service
  }
  override def paymentRecordService: PaymentRecordService = _paymentRecordService

  def personService_=(service: PersonService) = _personService = service
  override def personService: PersonService = _personService

  def productService_=(service: ProductService) = _productService = service
  override def productService: ProductService = _productService

  def profileStrengthService_=(service: ProfileStrengthService) =
    _profileStrengthService = service
  override def profileStrengthService: ProfileStrengthService =
    _profileStrengthService

  def orgService_=(service: OrganisationService) = {
    _orgService = service
  }
  override def orgService: OrganisationService = _orgService

  def socialProfileService_=(service: SocialProfileService) = _socialProfileService = service
  override def socialProfileService: SocialProfileService = _socialProfileService

  def translationService_=(service: TranslationService) = _translationService = service
  override def translationService: TranslationService = _translationService

  def userAccountService_=(service: UserAccountService) = _userAccountService = service
  override def userAccountService: UserAccountService = _userAccountService

}
