name := "happymelly-teller"

version := "1.7-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions += "-feature"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

libraryDependencies ++= Seq(
    cache,
    ws,
    filters,
    specs2 % Test,
    "be.objectify" %% "deadbolt-scala" % "2.4.2",
    "com.andersen-gott" %% "scravatar" % "1.0.3", //gravatar support
    "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.0", //image resizing
    "com.github.mumoshu" %% "play2-memcached-play24" % "0.7.0",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
    "com.typesafe.play" %% "play-slick" % "1.1.1",
    "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
    "com.typesafe.play" %% "play-mailer" % "3.0.1",
    "joda-time" % "joda-time" % "2.8.1",
    "org.joda" % "joda-convert" % "1.7",
    "mysql" % "mysql-connector-java" % "5.1.34",
    "org.apache.poi" % "poi" % "3.9",
    "org.apache.poi" % "poi-ooxml" % "3.9",
    "org.scalamock" %% "scalamock-specs2-support" % "3.2.2" % Test,
    "org.joda" % "joda-money" % "0.9",
    "org.pegdown" % "pegdown" % "1.4.2",
    "org.planet42" %% "laika-core" % "0.5.1",
    "org.jsoup" % "jsoup" % "1.7.3",
    "ws.securesocial" %% "securesocial" % "3.0-M4",
    "net.kaliber" %% "play-s3" % "7.0.2" //s3 amazon support
  )

routesGenerator := InjectedRoutesGenerator

libraryDependencies += evolutions

resolvers += Resolver.sonatypeRepo("releases")

resolvers += "Kaliber Internal Repository" at "https://jars.kaliber.io/artifactory/libs-release-local"

resolvers += "Spy Repository" at "http://files.couchbase.com/maven2"

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

includeFilter in(Assets, LessKeys.less) := "*.less"

LessKeys.compress in Assets := true

// disable publishing the main API jar
publishArtifact in(Compile, packageDoc) := false

// disable publishing the main sources jar
publishArtifact in(Compile, packageSrc) := false

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting in Test := false

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages in Test := "<empty>;Reverse.*;.*AuthService.*;models\\.database\\..*;.*routes_.*"

sources in(Compile, doc) := Seq.empty

fork in run := true