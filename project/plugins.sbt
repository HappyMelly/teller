logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("play" % "sbt-plugin" % "2.1.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.1")
