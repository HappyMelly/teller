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

import models.service.Services

trait FakeServices extends Services {

  private var _brandService = new FakeBrandService
  private var _contributionService = new FakeContributionService
  private var _evaluationService = new StubEvaluationService
  private var _eventService = new StubEventService
  private var _eventTypeService = new FakeEventTypeService
  private var _feeService = new FakeBrandFeeService
  private var _licenseService = new FakeLicenseService
  private var _memberService = new FakeMemberService
  private var _orgService = new FakeOrganisationService
  private var _paymentRecordService = new FakePaymentRecordService
  private var _personService = new FakePersonService
  private var _productService = new FakeProductService
  private var _userAccountService = new FakeUserAccountService

  def brandService_=(service: FakeBrandService) = {
    _brandService = service
  }

  override def brandService: FakeBrandService = _brandService

  def contributionService_=(service: FakeContributionService) = {
    _contributionService = service
  }

  override def contributionService: FakeContributionService = _contributionService

  def evaluationService_=(service: StubEvaluationService) = {
    _evaluationService = service
  }

  override def evaluationService: StubEvaluationService = _evaluationService

  def eventService_=(service: StubEventService) = {
    _eventService = service
  }
  override def eventService: StubEventService = _eventService

  def eventTypeService_=(service: FakeEventTypeService) = _eventTypeService = service
  override def eventTypeService: FakeEventTypeService = _eventTypeService

  def feeService_=(service: FakeBrandFeeService) = _feeService = service
  override def feeService: FakeBrandFeeService = _feeService

  def licenseService_=(service: FakeLicenseService) = {
    _licenseService = service
  }

  override def licenseService: FakeLicenseService = _licenseService

  def memberService_=(service: FakeMemberService) = {
    _memberService = service
  }

  override def memberService: FakeMemberService = _memberService

  def paymentRecordService_=(service: FakePaymentRecordService) = {
    _paymentRecordService = service
  }
  override def paymentRecordService: FakePaymentRecordService = _paymentRecordService

  def personService_=(service: FakePersonService) = {
    _personService = service
  }

  override def personService: FakePersonService = _personService

  def productService_=(service: FakeProductService) = {
    _productService = service
  }

  override def productService: FakeProductService = _productService

  def orgService_=(service: FakeOrganisationService) = {
    _orgService = service
  }

  override def orgService: FakeOrganisationService = _orgService

  def userAccountService_=(service: FakeUserAccountService) = {
    _userAccountService = service
  }

  override def userAccountService: FakeUserAccountService = _userAccountService

}
