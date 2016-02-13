package security

import javax.inject.Singleton

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import models.repository.IRepositories
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

/**
  * Created by sery0ga on 02/02/16.
  */

@Singleton
class TellerHandlerCache  @javax.inject.Inject() (implicit val env: TellerRuntimeEnvironment,
                                                  val messagesApi: MessagesApi,
                                                  val services: IRepositories) extends HandlerCache {

  val defaultHandler: DeadboltHandler = new AuthorisationHandler()(env, messagesApi, services)

  val handlers: Map[Any, DeadboltHandler] = Map(HandlerKeys.defaultHandler -> defaultHandler)

  override def apply(): DeadboltHandler = defaultHandler

  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}