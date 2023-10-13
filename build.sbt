import sbt.Keys.{homepage, licenses, organization}
import xerial.sbt.Sonatype.GitHubHosting
import xerial.sbt.Sonatype.autoImport.sonatypeProjectHosting

lazy val root = project.in(file("."))
  .settings(
    name := "sbt-idea-shell",
    organization := "org.jetbrains.scala",

    sbtPlugin := true,

    initialCommands := """import org.jetbrains._""",

    // Sonatype settings
    sonatypeProfileName := "org.jetbrains",
    homepage := Some(url("https://github.com/JetBrains/sbt-structure")),
    sonatypeProjectHosting := Some(GitHubHosting("JetBrains", "sbt-structure", "scala-developers@jetbrains.com")),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")),

    // set up 'scripted; sbt plugin for testing sbt plugins
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),

    // publishing boilerplate
    crossSbtVersions := Nil, // handled by explicitly setting sbtVersion via scalaVersion
    crossScalaVersions := Seq("2.12.11", "2.10.7"),
    pluginCrossBuild / sbtVersion := {
      // keep this as low as possible to avoid running into binary incompatibility such as https://github.com/sbt/sbt/issues/5049
      scalaBinaryVersion.value match {
        case "2.10" => "0.13.17"
        case "2.12" => "1.2.1"
      }
    }
  )
  .enablePlugins(ScriptedPlugin)