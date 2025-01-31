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

// Use 2 digits version to match the published artifact
// This is primarily needed for sbt 2.0.0-<tag> version, which we publish with `_sbt2.0` suffix
// instead of the default, full suffix `_sbt2.0.0-<tag>`
pluginCrossBuild / sbtBinaryVersion := {
  val sbtVersion3Digits = (pluginCrossBuild / sbtVersion).value
  val sbtVersion2Digits = sbtVersion3Digits.substring(0, sbtVersion3Digits.lastIndexOf("."))
  sbtVersion2Digits
}
