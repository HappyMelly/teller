import play.PlayImport.PlayKeys._

name := "happymelly-teller"

version :="1.6-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions += "-feature"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++=
  Seq(
    jdbc,
    cache,
    ws,
    filters,
    "com.andersen-gott" %% "scravatar" % "1.0.3",
    "org.scalamock" %% "scalamock-specs2-support" % "3.2.1" % "test",
    "be.objectify" %% "deadbolt-scala" % "2.3.3",
    "com.github.mumoshu" %% "play2-memcached" % "0.5.0-RC1",
    "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
    "com.typesafe.play" %% "play-slick" % "0.8.0",
    "joda-time" % "joda-time" % "2.4",
    "org.joda" % "joda-convert" % "1.6",
    "mysql" % "mysql-connector-java" % "5.1.34",
    "org.apache.poi" % "poi" % "3.9",
    "org.apache.poi" % "poi-ooxml" % "3.9",
    "org.joda" % "joda-money" % "0.9",
    "org.pegdown" % "pegdown" % "1.4.2",
    "org.planet42" %% "laika-core" % "0.5.0",
    "org.jsoup" % "jsoup" % "1.7.3",
    // update selenium to avoid browser test to hang
    "org.seleniumhq.selenium" % "selenium-java" % "2.39.0",
    "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
    "nl.rhinofly" %% "play-s3" % "6.0.0"
  )

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local"

routesImport += "binders._"

herokuAppName in Compile := Map(
  "test" -> "teller-test",
  "prod" -> "teller-prod"
).getOrElse(sys.props("appEnv"), "teller-test")

herokuIncludePaths in Compile := Seq(
  "app", "conf", "lib", "public"
)

herokuProcessTypes in Compile := Map(
  "web" -> "target/universal/stage/bin/happymelly-teller -Dconfig.file=conf/$CONF_FILENAME -Dhttp.port=$PORT"
)

// disable publishing the main API jar
publishArtifact in (Compile, packageDoc) := false

// disable publishing the main sources jar
publishArtifact in (Compile, packageSrc) := false

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;.*AuthService.*;models\\.database\\..*;.*routes_.*"

sources in (Compile, doc) := Seq.empty

fork in run := true