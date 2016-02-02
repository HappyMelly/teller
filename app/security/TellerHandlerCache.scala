package security

import javax.inject.Singleton

import be.objectify.deadbolt.scala.{HandlerKey, DeadboltHandler}
import be.objectify.deadbolt.scala.cache.HandlerCache
import play.api.i18n.MessagesApi
import services.TellerRuntimeEnvironment

/**
  * Created by sery0ga on 02/02/16.
  */

@Singleton
class TellerHandlerCache  @javax.inject.Inject() (implicit val env: TellerRuntimeEnvironment,
                                                  val messagesApi: MessagesApi) extends HandlerCache {

  val defaultHandler: DeadboltHandler = new AuthorisationHandler()(env, messagesApi)

  val handlers: Map[Any, DeadboltHandler] = Map(HandlerKeys.defaultHandler -> defaultHandler)

  override def apply(): DeadboltHandler = defaultHandler

  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}