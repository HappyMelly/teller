package services.cleaners

import models.service.Services
import org.joda.time.LocalDate

/**
  * Removes expired event requests from database
  */
object ExpiredEventRequestCleaner extends Services {

  def clean() = {
    println("ExpiredEventRequestClearer: start")
    eventRequestService.deleteExpired(LocalDate.now())
    println("ExpiredEventRequestClearer: end")
  }
}
