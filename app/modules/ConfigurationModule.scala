package modules

import be.objectify.deadbolt.scala.cache.HandlerCache
import configuration.{IScheduler, Scheduler}
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import securesocial.core.RuntimeEnvironment
import security.TellerHandlerCache
import services.TellerRuntimeEnvironment

/**
  * Created by sery0ga on 02/02/16.
  */
class ConfigurationModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[IScheduler].to[Scheduler].eagerly(),
    bind[HandlerCache].to[TellerHandlerCache],
    bind[RuntimeEnvironment].to[TellerRuntimeEnvironment]
  )
}
