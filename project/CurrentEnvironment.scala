import sbt.File
import sbt.io.syntax.fileToRichFile

import java.io.File

object CurrentEnvironment {

  val OsName: String = System.getProperty("os.name").toLowerCase
  val UserHome: File = new File(System.getProperty("user.home")).getCanonicalFile.ensuring(_.exists())
  val WorkingDir: File = new File(".").getCanonicalFile

  val CurrentJavaHome: File = new File(System.getProperty("java.home"))
  val CurrentJavaExecutablePath: String = (CurrentJavaHome / "bin/java").getCanonicalPath

  private val PossibleJvmLocations: Seq[File] =
    if (OsName.contains("mac")) Seq(
      new File("/Library/Java/JavaVirtualMachines"),
      new File(s"${UserHome.getPath}/Library/Java/JavaVirtualMachines")
    )
    else if (OsName.contains("linux")) Seq(
      new File("/usr/lib/jvm"),
      new File("/usr/java")
    )
    else if (OsName.contains("win")) Seq(
      new File("C:\\Program Files\\Java"),
      new File("C:\\Program Files (x86)\\Java")
    )
    else
      throw new UnsupportedOperationException("Unknown operating system.")

  val JavaOldHome: File = findJvmInstallation("1.8")
    .orElse(findJvmInstallation("11"))
    .getOrElse {
      throw new IllegalStateException(s"Java 1.8 or 11 not found in default locations:\n${PossibleJvmLocations.mkString("\n")}")
    }
  val JavaOldExecutablePath: String = (JavaOldHome / "bin/java").getCanonicalPath

  val SbtGlobalRoot: File = new File(UserHome, ".sbt-structure-global").getCanonicalFile

  println(
    s"""java home       : $CurrentJavaHome
       |java old home   : $JavaOldHome
       |sbt global root : $SbtGlobalRoot
       |see sbt-launcher logs in $SbtGlobalRoot/boot/update.log""".stripMargin
  )

  //TODO: replace with sbt.Keys.discoveredJavaHomes once this PR is merged and published
  // https://github.com/sbt/sbt/pull/8032
  private def findJvmInstallation(javaVersion: String): Option[File] = {
    val jvmFolder = PossibleJvmLocations
      .flatMap { folder =>
        val dirs = Option(folder.listFiles()).getOrElse(Array.empty).filter(_.isDirectory)
        dirs.filter(_.getName.contains(javaVersion))
      }
      .headOption
      .map { root =>
        if (OsName.contains("mac"))
          root / "/Contents/Home"
        else
          root
      }

    jvmFolder.filter(_.exists())
  }

  lazy val getIvyHomeVmOptionForTeamcity: Seq[String] =
    if (CurrentEnvironment.isRunningOnTeamcity) {
      val ivyHomeResult = CurrentEnvironment.detectIvyHomeOnTeamcity
      ivyHomeResult.fold(
        error => {
          System.err.println(s"Failed to detect Ivy cache directory on TeamCity: $error")
          Nil
        },
        dir => {
          println(s"Ivy cache directory on TeamCity: $dir")
          Seq(s"-Dsbt.ivy.home=$dir")
        }
      )
    }
    else Nil

  private def isRunningOnTeamcity: Boolean = System.getenv("TEAMCITY_VERSION") != null

  
  /**
   * Sbt runner on teamcity uses a different ivy cache directory.
   * Unfortunately, it's hardcoded and is not published via TC parameters, so we can't read it here
   *
   * @see jetbrains.buildServer.sbt.SbtRunnerBuildService#getVMProperties
   * @see jetbrains.buildServer.sbt.IvyCacheProvider#IvyCacheProvider
   */
  private def detectIvyHomeOnTeamcity: Either[String, File] = {
    val TeamcityAgentHomeDirProperty = "agent.home.dir"
    for {
      tcHome <- Option(System.getProperty(TeamcityAgentHomeDirProperty)).toRight(s"Undefined property: $TeamcityAgentHomeDirProperty")
      ivyHomeDirFile = new File(tcHome, "system/sbt_ivy")
      ivyHomeDir <- Option(ivyHomeDirFile).filter(_.exists()).toRight(s"Ivy cache directory doesn't exist: $ivyHomeDirFile")
    } yield ivyHomeDir
  }
}
