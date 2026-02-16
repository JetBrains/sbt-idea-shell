import sbt.File

object CurrentEnvironment {

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
