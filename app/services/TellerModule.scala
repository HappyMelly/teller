package services

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import securesocial.core.RuntimeEnvironment

/**
  * Injector module
  */
class TellerModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[RuntimeEnvironment].to[TellerRuntimeEnvironment]
  )
}
