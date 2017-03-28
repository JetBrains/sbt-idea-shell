name := "sbt-idea-shell"
organization := "org.jetbrains"
version := "1.0"
scalaVersion := "2.10.6"

sbtPlugin := true

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

publishMavenStyle := false
bintrayOrganization := Some("jetbrains")
bintrayRepository := "sbt-plugins"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

initialCommands := """import org.jetbrains._"""

// set up 'scripted; sbt plugin for testing sbt plugins
ScriptedPlugin.scriptedSettings
scriptedLaunchOpts ++=
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
