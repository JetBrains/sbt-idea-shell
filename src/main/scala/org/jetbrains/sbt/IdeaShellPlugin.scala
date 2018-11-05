package org.jetbrains.sbt

import sbt.Keys.commands
import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, Def, PluginTrigger, Plugins}
import pluginCommands._

/**
  * Created by jast on 2017-03-28.
  */
object IdeaShellPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = JvmPlugin

  object autoImport {}

  override lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    // history is provided by builtin shell, don't spam regular history with generated commands
    sbt.Keys.historyPath := None
  )

  override lazy val buildSettings: Seq[Def.Setting[_]] = Seq(
    commands += ideaShell
  )

  override def globalSettings: Seq[Def.Setting[_]] = Seq()
}
