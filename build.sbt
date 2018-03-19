name := "sbt-idea-shell"
organization := "org.jetbrains"

sbtPlugin := true

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

publishMavenStyle := false
bintrayOrganization := Some("jetbrains")
bintrayRepository := "sbt-plugins"
bintrayVcsUrl := Option("https://github.com/JetBrains/sbt-idea-shell")

initialCommands := """import org.jetbrains._"""

// set up 'scripted; sbt plugin for testing sbt plugins
scriptedLaunchOpts ++=
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)

crossSbtVersions := Seq("0.13.17", "1.1.1")
