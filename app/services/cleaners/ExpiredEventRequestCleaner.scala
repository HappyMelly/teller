package services.cleaners

import models.repository.Repositories
import org.joda.time.LocalDate

/**
  * Removes expired event requests from database
  */
class ExpiredEventRequestCleaner(services: Repositories) {

  def clean() = {
    println("ExpiredEventRequestClearer: start")
    services.eventRequest.deleteExpired(LocalDate.now())
    println("ExpiredEventRequestClearer: end")
  }
}
