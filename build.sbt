import lmcoursier.internal.shaded.coursier.core.Version
import sbt.{Def, url}

val Scala210 = "2.10.7"
val Scala212 = "2.12.21"
val Scala3 = "3.7.4"

val SbtVersion_0_13 = "0.13.18"
// keep this as low as possible
// to avoid running into binary incompatibility such as https://github.com/sbt/sbt/issues/5049
val SbtVersion_1_0 = "1.0.0"
val SbtVersion_2 = "2.0.0-RC8"

val SbtVersion_1_LatestForTests = "1.12.3"

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

    crossScalaVersions := Seq(
      Scala210,
      Scala212,
      Scala3
    ),

    sbtVersion := {
      scalaVersion.value match {
        case `Scala210` => SbtVersion_0_13
        case `Scala212` => SbtVersion_1_0
        case `Scala3`   => SbtVersion_2
        case v =>
          throw new IllegalArgumentException(s"Unsupported scalaVersion: $v")
      }
    },
    scalacOptions ++= {
      Seq("-deprecation", "-feature") ++ {
        scalaVersion.value match {
          case `Scala212` | `Scala3` =>
            // TODO: Set `--release 17` for Scala 3 when we start compiling against Scala 3.8+/Sbt 2.0.0-RC9+
            Seq("--release", "8")
          case _ => Seq.empty
        }
      }
    },
    // TODO: Set `--release 17` for Scala 3 when we start compiling against Scala 3.8+/Sbt 2.0.0-RC9+
    javacOptions ++= Seq("--release", "8"),
    Compile / unmanagedSourceDirectories ++= {
      val sbtVersion = Version((pluginCrossBuild / sbtBinaryVersion).value)
      val baseDir = (Compile / sourceDirectory).value
      // Add a source directory which is used by both sbt 1 and sbt 2.
      if (sbtVersion >= Version("1.0"))
        Seq(baseDir / "scala-sbt-1+")
      else
        Seq.empty
    },
  )

addCommandAlias("runScriptedTestsAll", s"""; runScriptedTestsScala3; runScriptedTestsScala212; runScriptedTestsScala210""")
addCommandAlias("runScriptedTestsScala3", s"""; set scalaVersion := "$Scala3"; scripted sbt-idea-shell/shell-command-sbt2""")
addCommandAlias("runScriptedTestsScala212", s"""; set scalaVersion := "$Scala212"; scripted sbt-idea-shell/shell-command""")
addCommandAlias("runScriptedTestsScala210", s"""; set scalaVersion := "$Scala210"; scripted sbt-idea-shell/shell-command""")