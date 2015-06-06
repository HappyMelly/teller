scalacOptions += "-feature"

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

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;.*AuthService.*;models\\.database\\..*"

libraryDependencies ++=
  Seq(
    "com.andersen-gott" %% "scravatar" % "1.0.3",
    "org.scalamock" %% "scalamock-specs2-support" % "3.2.1" % "test"
  )
