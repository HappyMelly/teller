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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers.acceptance.events

import _root_.integration.PlayAppSpec
import controllers.Events
import helpers.{EventHelper, OrganisationHelper}
import models.service.event.EventInvoiceService
import models.service.{EventService, OrganisationService}
import models.{EventInvoice, EventView}
import org.scalamock.specs2.IsolatedMockFactory
import stubs._

class EventsSpec extends PlayAppSpec with IsolatedMockFactory {

  override def is = s2"""

  Given an event exists
    when its confirmation is requested
      then it should be confirmed                                            $e2

  Given an event doesn't exist
    when its invoice update is requested
      then an error should be returned                                       $e3

  Given an event exists
    when its invoice update is requested
      then invoice number and recipient org should be updated                $e4
  """

  class TestEvents extends Events(FakeRuntimeEnvironment)
    with FakeActivities
    with FakeServices
    with FakeSecurity

  val controller = new TestEvents
  val eventService = mock[EventService]
  controller.eventService_=(eventService)

  def e2 = {
    (services.eventService.confirm _) expects 1L
    val result = controller.confirm(1L).apply(fakePostRequest())
    header("Location", result) must beSome.which(_.contains("/event/1"))
  }

  def e3 = {
    (services.eventService.findWithInvoice _) expects 1L returning None
    val result = controller.invoice(1L).apply(fakePostRequest())
    status(result) must equalTo(NOT_FOUND)
  }

  def e4 = {
    val id = 2L
    val invoice = EventInvoice(Some(1L), Some(id), 5L, None, None)
    val view = EventView(EventHelper.one, invoice)
    (services.eventService.findWithInvoice _) expects id returning Some(view)
    val invoiceService = mock[EventInvoiceService]
    (invoiceService.update _) expects invoice.copy(invoiceBy = Some(6L), number = Some("31"))
    controller.eventInvoiceService_=(invoiceService)
    val orgService = mock[OrganisationService]
    (services.orgService.find(_: Long)) expects 6L returning Some(OrganisationHelper.one)
    controller.orgService_=(orgService)
    val request = fakePostRequest().
      withFormUrlEncodedBody(("invoiceBy", "6"), ("number", "31"))
    val result = controller.invoice(id).apply(request)

    header("Location", result) must beSome.which(_.contains("/event/2"))
  }
}