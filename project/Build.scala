import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "melly-platform"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "mysql" % "mysql-connector-java" % "5.1.21",
    // update selenium to avoid browser test to hang
    "org.seleniumhq.selenium" % "selenium-java" % "2.32.0"

  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
