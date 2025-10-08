package org.jetbrains.sbt

import org.jetbrains.sbt.constants._
import sbt.BasicCommandStrings.ShellDetailed
import sbt._

object pluginCommands {

  private val IdeaShellExec = IdeaShellCommandString

  // copied and adapted from shell command in sbt.BasicCommands
  def ideaShell: Command = Command.command(IdeaShellCommandString, Help.more(IdeaShellCommandString, ShellDetailed)) { s =>
    val userPrompt = s.get(BasicKeys.shellPrompt) match {
      case Some(pf) => pf(s)
      case None => "> "
    }
    val prompt = IdeaPromptMarker + userPrompt

    val reader = new FullReader(None, s.combinedParser)
    val line = reader.readLine(prompt)
    line match {
      case Some(cmd) =>
        val newState = s.copy(
          onFailure = Some(IdeaShellExec),
          remainingCommands = cmd +: IdeaShellExec +: s.remainingCommands
        ).setInteractive(true)
        if (cmd.trim.isEmpty) newState else newState.clearGlobalLog
      case None => s.setInteractive(false)
    }
  }

}
