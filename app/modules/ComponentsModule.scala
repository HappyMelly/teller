package modules

import models.service.{Services, IServices}
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import services.integrations._

/**
  * Injector module
  */
class ComponentsModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[EmailComponent].to[Email],
    bind[IServices].to[Services]
  )
}