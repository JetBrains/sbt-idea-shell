package org.jetbrains.sbt

import sbt.BasicCommandStrings.{Shell,ShellDetailed}
import sbt.Keys.commands
import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, BasicKeys, Command, File, FullReader, Help, PluginTrigger}

/**
  * Created by jast on 2017-03-28.
  */
object IdeaShellPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires = JvmPlugin

  object autoImport {}

  override lazy val projectSettings = Seq()

  override lazy val buildSettings = Seq(
    commands += ideaShell
  )

  val IdeaShell: String = "idea-" + Shell

  // three invisible spaces ought to be enough for anyone
  val IDEA_PROMPT_MARKER = "[IJ]"

  // copied and adapted from shell command in sbt.BasicCommands
  private def ideaShell = Command.command(IdeaShell, Help.more(IdeaShell, ShellDetailed)) { s =>
    val history = (s get BasicKeys.historyPath) getOrElse Some(new File(s.baseDir, ".history"))
    val userPrompt = s get BasicKeys.shellPrompt match {
      case Some(pf) =>  pf(s)
      case None => "> "
    }
    val prompt = IDEA_PROMPT_MARKER + userPrompt

    val reader = new FullReader(history, s.combinedParser)
    val line = reader.readLine(prompt)
    line match {
      case Some(cmd) =>
        val newState = s.copy(
          onFailure = Some(IdeaShell),
          remainingCommands = cmd +: IdeaShell +: s.remainingCommands)
          .setInteractive(true)
        if (cmd.trim.isEmpty) newState else newState.clearGlobalLog
      case None => s.setInteractive(false)
    }
  }

  override def globalSettings = Seq()
}
