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

  private var _eventService = new StubEventService
  private var _personService = new FakePersonService
  private var _licenseService = new StubLicenseService
  private var _userAccountService = new StubUserAccountService
  private var _contributionService = new StubContributionService
  private var _productService = new StubProductService
  private var _evaluationService = new StubEvaluationService
  private var _memberService = new FakeMemberService
  private var _organisationService = new FakeOrganisationService

  def eventService_=(service: StubEventService) = {
    _eventService = service
  }

  override def eventService: StubEventService = _eventService

  def personService_=(service: FakePersonService) = {
    _personService = service
  }

  override def personService: FakePersonService = _personService

  def licenseService_=(service: StubLicenseService) = {
    _licenseService = service
  }

  override def licenseService: StubLicenseService = _licenseService

  def userAccountService_=(service: StubUserAccountService) = {
    _userAccountService = service
  }

  override def userAccountService: StubUserAccountService = _userAccountService

  def contributionService_=(service: StubContributionService) = {
    _contributionService = service
  }

  override def contributionService: StubContributionService = _contributionService

  def productService_=(service: StubProductService) = {
    _productService = service
  }

  override def productService: StubProductService = _productService

  def evaluationService_=(service: StubEvaluationService) = {
    _evaluationService = service
  }

  override def evaluationService: StubEvaluationService = _evaluationService

  def memberService_=(service: FakeMemberService) = {
    _memberService = service
  }

  override def memberService: FakeMemberService = _memberService

  def organisationService_=(service: FakeOrganisationService) = {
    _organisationService = service
  }

  override def organisationService: FakeOrganisationService = _organisationService
}
