scalacOptions += "-feature"

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false

libraryDependencies ++=
  Seq(
    "com.andersen-gott" %% "scravatar" % "1.0.3",
    "org.scalamock" %% "scalamock-specs2-support" % "3.2" % "test"
  )
