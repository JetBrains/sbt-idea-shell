name := "sbt-idea-shell"
organization := "org.jetbrains"

scalaVersion := (sbtVersionSeries.value match {
  case Sbt013 => "2.10.6"
  case Sbt1 => "2.12.2"
})

sbtPlugin := true

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

publishMavenStyle := false
bintrayOrganization := Some("jetbrains")
bintrayRepository := "sbt-plugins"
bintrayVcsUrl := Option("https://github.com/JetBrains/sbt-idea-shell")
bintrayCredentialsFile in Global := file(".credentials")

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

initialCommands := """import org.jetbrains._"""

// set up 'scripted; sbt plugin for testing sbt plugins
//ScriptedPlugin.scriptedSettings

crossSbtVersions := Seq("0.13.15", "1.0.0-RC2")


// workaround for https://github.com/sbt/sbt/issues/3325 -- remove when it's fixed
ScriptedPlugin.scriptedSettings.filterNot(_.key.key.label == libraryDependencies.key.label)

libraryDependencies ++= {
  CrossVersion.binarySbtVersion(scriptedSbt.value) match {
    case "0.13" =>
      Seq(
        "org.scala-sbt" % "scripted-sbt" % scriptedSbt.value % scriptedConf.toString,
        "org.scala-sbt" % "sbt-launch" % scriptedSbt.value % scriptedLaunchConf.toString
      )
    case _ =>
      Seq(
        "org.scala-sbt" %% "scripted-sbt" % scriptedSbt.value % scriptedConf.toString,
        "org.scala-sbt" % "sbt-launch" % scriptedSbt.value % scriptedLaunchConf.toString
      )
  }
}
// --- end workaround

scriptedLaunchOpts ++=
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
