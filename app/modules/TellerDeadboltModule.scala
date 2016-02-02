package modules

import be.objectify.deadbolt.scala.cache.HandlerCache
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import security.TellerHandlerCache

/**
  * Created by sery0ga on 02/02/16.
  */
class TellerDeadboltModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[HandlerCache].to[TellerHandlerCache]
  )
}
