package security

import be.objectify.deadbolt.scala.HandlerKey

/**
  * Created by sery0ga on 02/02/16.
  */
object HandlerKeys {

  val defaultHandler = Key("defaultHandler")
  val dynamicHandler = Key("dynamicHandler")

  case class Key(name: String) extends HandlerKey
}
