import lmcoursier.internal.shaded.coursier.core.Version
import sbt.{Def, url}

val Scala210 = "2.10.7"
val Scala212 = "2.12.20"
val Scala3 = "3.7.2"

val SbtVersion_0_13 = "0.13.18"
// keep this as low as possible
// to avoid running into binary incompatibility such as https://github.com/sbt/sbt/issues/5049
val SbtVersion_1_0 = "1.0.0"
val SbtVersion_2 = "2.0.0-RC2"

val SbtVersion_1_LatestForTests = "1.11.4"

val sonatypeSettings: Seq[Def.Setting[?]] = Seq(
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),

  // Optional but nice-to-have
  organizationName := "JetBrains",
  organizationHomepage := Some(url("https://www.jetbrains.com/")),

  homepage := Some(url("https://github.com/JetBrains/sbt-idea-shell")),

  developers := List(
    Developer(
      id = "JetBrains",
      name = "JetBrains",
      email = "scala-developers@jetbrains.com",
      url = url("https://github.com/JetBrains")
    )
  ),

  scmInfo := Some(
    ScmInfo(
      url("https://github.com/JetBrains/sbt-idea-shell"),
      "scm:git:git@github.com:JetBrains/sbt-idea-shell.git",
      "scm:git:git@github.com:JetBrains/sbt-idea-shell.git"
    )
  ),
)

val scriptedTestsSettings: Seq[Def.Setting[?]] = Seq(
  // options used in "scripted" sbt plugin tests
  // (https://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins)
  scriptedLaunchOpts ++= Seq(
    "-Xmx1024M",
    s"-Dplugin.version=${version.value}",
  ) ++ CurrentEnvironment.getIvyHomeVmOptionForTeamcity,

  scripted / javaHome := Some(CurrentEnvironment.JavaOldHome),

  // NOTE: in practice `scripted / javaHome` doesn't work, and the jdk is not used in tests for older sbt versions.
  // In older sbt it will use the "java" command and use the globally installed JDK.
  // Right now the only way to work around this is to ensure that globally installed java is set to the desired.
  // But we instead just run the tests with the latest sbt version that doesn't have this issue and uses the javaHome
  scriptedSbt := {
    if (sbtVersion.value.startsWith("1"))
      SbtVersion_1_LatestForTests
    else
      scriptedSbt.value
  },
)

lazy val root = project.in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-idea-shell",
    organization := "org.jetbrains.scala",

    initialCommands := """import org.jetbrains._""",

    sonatypeSettings,
    scriptedTestsSettings,

    scalaVersion := Scala3,
    //    scalaVersion := Scala212,
    //    scalaVersion := Scala210,

    dependencyOverrides := Nil,
    crossScalaVersions := Seq(
      Scala210,
      Scala212,
      Scala3,
    ),
    crossSbtVersions := Nil, // handled by explicitly setting sbtVersion via scalaVersion
    sbtVersion := {
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
    scalacOptions ++= Seq("-deprecation", "-feature"),
    Compile / unmanagedSourceDirectories ++= {
      val sbtVersion = Version((pluginCrossBuild / sbtBinaryVersion).value)
      val baseDir = (Compile / sourceDirectory).value
      val dirs = Seq.newBuilder[File]

      if (sbtVersion >= Version("1.0"))
        dirs += (baseDir / "scala-sbt-1+")
      if (sbtVersion.repr.startsWith("1"))
        dirs += (baseDir / "scala-sbt-1.x")
      if (sbtVersion >= Version("2.0"))
        dirs += (baseDir / "scala-sbt-2.x")

      dirs.result()
    },
  )

addCommandAlias("runScriptedTestsAll", s""" ; runScriptedTestsScala3 ; runScriptedTestsScala212 ; runScriptedTestsScala210""")
addCommandAlias("runScriptedTestsScala3", s""" ; set scalaVersion := "$Scala3"   ; scripted sbt-idea-shell/shell-command-sbt2""")
addCommandAlias("runScriptedTestsScala212", s""" ; set scalaVersion := "$Scala212" ; scripted sbt-idea-shell/shell-command""")
addCommandAlias("runScriptedTestsScala210", s""" ; set scalaVersion := "$Scala210" ; scripted sbt-idea-shell/shell-command""")