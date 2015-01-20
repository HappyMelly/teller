logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.2.1")

addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.1.4")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.1")
