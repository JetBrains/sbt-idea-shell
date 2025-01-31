import lmcoursier.internal.shaded.coursier.core.Version
import sbt.Def
import xerial.sbt.Sonatype.GitHubHosting

val Scala210 = "2.10.7"
val Scala212 = "2.12.20"
val Scala3 = "3.6.2"

val SbtVersion_0_13 = "0.13.17"
// keep this as low as possible
// to avoid running into binary incompatibility such as https://github.com/sbt/sbt/issues/5049
val SbtVersion_1_0 = "1.0.0"
val SbtVersion_2 = "2.0.0-M3"

val sonatypeSettings: Seq[Def.Setting[?]] = Seq(
  sonatypeProfileName := "org.jetbrains",
  homepage := Some(url("https://github.com/JetBrains/sbt-structure")),
  sonatypeProjectHosting := Some(GitHubHosting("JetBrains", "sbt-structure", "scala-developers@jetbrains.com")),
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
)

lazy val root = project.in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-idea-shell",
    organization := "org.jetbrains.scala",

    initialCommands := """import org.jetbrains._""",

    sonatypeSettings,

    // options used in "scripted" sbt plugin tests
    // (https://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins)
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),

    scalaVersion := Scala212,
    crossScalaVersions := Seq(
      Scala210,
      Scala212,
      Scala3,
    ),
    crossSbtVersions := Nil, // handled by explicitly setting sbtVersion via scalaVersion
    pluginCrossBuild / sbtVersion := {
      val scalaVer = scalaVersion.value
      if (scalaVer == Scala210)
        SbtVersion_0_13
      else if (scalaVer == Scala212)
        SbtVersion_1_0
      else if (scalaVer == Scala3)
        SbtVersion_2
      else
        throw new IllegalArgumentException(s"Unsupported scalaBinaryVersion: $scalaVer")
    },
    // Note, due to this, we publish the artifact with `sbt_2.0` suffix even for unreleased sbt 2.0.0-<tag>
    // This goes against the default sbt behavior that uses `sbt_2.0.0-<tag>` suffix,
    // so the plugin can't be used in sbt in the usual way.
    // However, it's fine for us because the plugin is supposed to be manually added by the Scala Plugin
    pluginCrossBuild / sbtBinaryVersion := {
      val sbtVersion3Digits = (pluginCrossBuild / sbtVersion).value
      val sbtVersion2Digits = sbtVersion3Digits.substring(0, sbtVersion3Digits.lastIndexOf("."))
      sbtVersion2Digits
    },
    scalacOptions ++= Seq("-deprecation", "-feature"),
    Compile / unmanagedSourceDirectories ++= {
      val sbtVersion = Version((pluginCrossBuild / sbtBinaryVersion).value)
      val baseDir = (Compile / sourceDirectory).value
      if (sbtVersion >= Version("1.0"))
        Seq(baseDir / "scala-sbt-1+")
      else
        Nil
    },
  )