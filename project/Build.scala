import sbt._
import Keys._
import play.Project._
import com.typesafe.sbt.SbtScalariform.{ScalariformKeys, scalariformSettings}
import scalariform.formatter.preferences._

object ApplicationBuild extends Build {

  val appName         = "melly-platform"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    "com.github.tototoshi" %% "slick-joda-mapper" % "0.3.0",
    "com.typesafe.play" %% "play-slick" % "0.3.3",
    "com.typesafe.slick" %% "slick" % "1.0.1",
    "mysql" % "mysql-connector-java" % "5.1.21",
    // update selenium to avoid browser test to hang
    "org.seleniumhq.selenium" % "selenium-java" % "2.32.0",
    "securesocial" %% "securesocial" % "master-SNAPSHOT",
    "org.pegdown" % "pegdown" % "1.2.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(scalariformSettings :_*).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  ).settings(
    /* Scalariform: override default settings - no spaces within pattern binders is the only option in IntelliJ IDEA,
      preserve spaces before arguments is needed for infix function syntax (unconfirmed).*/
    ScalariformKeys.preferences := FormattingPreferences().
      setPreference(SpacesWithinPatternBinders, false).
      setPreference(RewriteArrowSymbols, true).
      setPreference(PreserveSpaceBeforeArguments, true)
  )
}
