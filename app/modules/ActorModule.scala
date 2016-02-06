package modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import services.integrations.EmailActor

/**
  * Created by sery0ga on 06/02/16.
  */
class ActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[EmailActor]("email")
  }
}
