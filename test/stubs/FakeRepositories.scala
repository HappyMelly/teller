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

import models.repository._
import models.repository.admin.ApiTokenRepository
import models.repository.brand._
import models.repository.cm._
import models.repository.cm.brand.{EventTypeRepository, CertificateTemplateRepository, BrandFeeRepository, BrandCoordinatorRepository}
import models.repository.cm.event.{EventInvoiceRepository, EventCancellationRepository}
import models.repository.event._

trait FakeRepositories extends Repositories {

  private var _activityService = new ActivityRepository
  private var _apiTokenService = new ApiTokenRepository
  private var _brandService = new BrandRepository
  private var _brandTeamMemberService = new BrandCoordinatorRepository
  private var _certificateService = new CertificateTemplateRepository
  private var _contributionService = new ContributionRepository
  private var _evaluationService = new EvaluationRepository
  private var _eventService = new EventRepository
  private var _eventCancellationService = new EventCancellationRepository
  private var _eventInvoiceService = new EventInvoiceRepository
  private var _eventTypeService = new EventTypeRepository
  private var _experimentService = new ExperimentRepository
  private var _facilitatorService = new FacilitatorRepository
  private var _feeService = new BrandFeeRepository
  private var _licenseService = new LicenseRepository
  private var _memberService = new MemberRepository
  private var _orgService = new OrganisationRepository
  private var _paymentRecordService = new PaymentRecordRepository
  private var _personService = new PersonRepository
  private var _productService = new ProductRepository
  private var _profileStrengthService = new ProfileStrengthRepository
  private var _socialProfileService = new SocialProfileRepository
  private var _userAccountService = new UserAccountRepository
  private var _userIdentityService = new IdentityRepository

  def activityService_=(service: ActivityRepository) = _activityService = service
  override def activity: ActivityRepository = _activityService

  def apiTokenService_=(service: ApiTokenRepository) = _apiTokenService = service
  override def apiToken: ApiTokenRepository = _apiTokenService

  def brandService_=(service: BrandRepository) = _brandService = service
  override def brand: BrandRepository = _brandService

  def brandCoordinatorService_=(service: BrandCoordinatorRepository) =
    _brandTeamMemberService = service
  override def brandCoordinator = _brandTeamMemberService

  def certificateService_=(service: CertificateTemplateRepository) = _certificateService = service
  override def certificate: CertificateTemplateRepository = _certificateService

  def contributionService_=(service: ContributionRepository) = _contributionService = service
  override def contribution: ContributionRepository = _contributionService

  def evaluationService_=(service: EvaluationRepository) = _evaluationService = service
  override def evaluation: EvaluationRepository = _evaluationService

  def eventService_=(service: EventRepository) = _eventService = service
  override def event: EventRepository = _eventService

  def eventCancellationService_=(service: EventCancellationRepository) =
    _eventCancellationService = service
  override def eventCancellation: EventCancellationRepository = _eventCancellationService

  def eventInvoiceService_=(service: EventInvoiceRepository) = _eventInvoiceService = service
  override def eventInvoice: EventInvoiceRepository = _eventInvoiceService

  def eventTypeService_=(service: EventTypeRepository) = _eventTypeService = service
  override def eventType: EventTypeRepository = _eventTypeService

  def experimentService_=(service: ExperimentRepository) = _experimentService = service
  override def experiment: ExperimentRepository = _experimentService

  def facilitatorService_=(service: FacilitatorRepository) = _facilitatorService = service
  override def facilitator: FacilitatorRepository = _facilitatorService

  def feeService_=(service: BrandFeeRepository) = _feeService = service
  override def fee: BrandFeeRepository = _feeService

  def licenseService_=(service: LicenseRepository) = _licenseService = service
  override def license: LicenseRepository = _licenseService

  def memberService_=(service: MemberRepository) = _memberService = service
  override def member: MemberRepository = _memberService

  def paymentRecordService_=(service: PaymentRecordRepository) = {
    _paymentRecordService = service
  }
  override def paymentRecord: PaymentRecordRepository = _paymentRecordService

  def personService_=(service: PersonRepository) = _personService = service
  override def person: PersonRepository = _personService

  def productService_=(service: ProductRepository) = _productService = service
  override def product: ProductRepository = _productService

  def profileStrengthService_=(service: ProfileStrengthRepository) =
    _profileStrengthService = service
  override def profileStrength: ProfileStrengthRepository =
    _profileStrengthService

  def orgService_=(service: OrganisationRepository) = {
    _orgService = service
  }
  override def org: OrganisationRepository = _orgService

  def socialProfileService_=(service: SocialProfileRepository) = _socialProfileService = service
  override def socialProfile: SocialProfileRepository = _socialProfileService

  def userAccountService_=(service: UserAccountRepository) = _userAccountService = service
  override def userAccount: UserAccountRepository = _userAccountService

  def identityService_=(service: IdentityRepository) = _userIdentityService = service
  override def identity: IdentityRepository = _userIdentityService

}
