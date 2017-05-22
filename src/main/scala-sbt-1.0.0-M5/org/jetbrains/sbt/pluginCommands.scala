package org.jetbrains.sbt

import org.jetbrains.sbt.constants._
import sbt.BasicCommandStrings.ShellDetailed
import sbt.{BasicKeys, Command, File, FullReader, Help, Exec}


object pluginCommands {

  private val shellExec = Exec(IdeaShell, None)

  // copied and adapted from shell command in sbt.BasicCommands
  def ideaShell: Command = Command.command(IdeaShell, Help.more(IdeaShell, ShellDetailed)) { s =>
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
          onFailure = Some(shellExec),
          remainingCommands = Exec(cmd, s.source) +: shellExec +: s.remainingCommands
        ).setInteractive(true)
        if (cmd.trim.isEmpty) newState else newState.clearGlobalLog
      case None => s.setInteractive(false)
    }
  }

}
