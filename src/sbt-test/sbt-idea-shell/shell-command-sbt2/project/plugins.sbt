{
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginVersion != null) {
    // TODO : OK, I got why this doens't work
    //  in build.sbt I change the bin version to pretend as if we publish for a stable version for sbt
    //  But here sbt doesn't know that and tries to use the full version 2.0.0-M3 as this version is used for tests
    addSbtPlugin("org.jetbrains.scala" % "sbt-idea-shell" % pluginVersion)
  } else {
    throw new RuntimeException(
      """|The system property 'plugin.version' is not defined.
         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  }
}
