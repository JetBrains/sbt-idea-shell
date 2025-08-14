package org.jetbrains.sbt

import org.jetbrains.sbt.constants.*
import sbt.BasicCommandStrings.ShellDetailed
import sbt.*

object pluginCommands {

  private val IdeaShellExec = Exec(IdeaShellCommandString, None)

  /**
   * copied and adapted from `shell` command in [[sbt.BasicCommands]] (since `1.x` renamed to "oldshell")
   */
  def ideaShell: Command = Command.command(IdeaShellCommandString, Help.more(IdeaShellCommandString, ShellDetailed)) { s =>
    val history = s.get(BasicKeys.historyPath).getOrElse(Some(new File(s.baseDir, ".history")))
    val userPrompt = s.get(BasicKeys.shellPrompt) match {
      case Some(pf) => pf(s)
      case None => "> "
    }
    val prompt = IdeaPromptMarker + userPrompt

    val reader = FullReaderCompat.newFullReader(history, s)
    val line = reader.readLine(prompt)
    line match {
      case Some(cmd) =>
        val newState = s.copy(
          onFailure = Some(IdeaShellExec),
          remainingCommands = Exec(cmd, s.source) +: IdeaShellExec +: s.remainingCommands
        ).setInteractive(true)
        if (cmd.trim.isEmpty) newState else newState.clearGlobalLog
      case None => s.setInteractive(false)
    }
  }
}
