scalacOptions += "-feature"

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false

libraryDependencies ++=
  Seq(
    "com.andersen-gott" %% "scravatar" % "1.0.3"
  )
