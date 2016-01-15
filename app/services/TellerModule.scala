package services

import com.google.inject.{TypeLiteral, AbstractModule}
import net.codingwell.scalaguice.ScalaModule
import securesocial.core.RuntimeEnvironment

/**
  * Injector module
  */
class TellerModule extends AbstractModule with ScalaModule {
  override def configure() {
    val environment: TellerRuntimeEnvironment = new TellerRuntimeEnvironment
    bind(new TypeLiteral[RuntimeEnvironment] {}).toInstance(environment)
  }
}
