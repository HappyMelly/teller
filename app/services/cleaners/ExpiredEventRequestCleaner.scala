package services.cleaners

import models.service.Services
import org.joda.time.LocalDate

/**
  * Removes expired event requests from database
  */
class ExpiredEventRequestCleaner(services: Services) {

  def clean() = {
    println("ExpiredEventRequestClearer: start")
    services.eventRequestService.deleteExpired(LocalDate.now())
    println("ExpiredEventRequestClearer: end")
  }
}
