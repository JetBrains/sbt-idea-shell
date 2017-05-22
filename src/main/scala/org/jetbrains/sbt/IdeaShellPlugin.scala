package org.jetbrains.sbt

import sbt.Keys.commands
import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, PluginTrigger}
import pluginCommands._

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

  override def globalSettings = Seq()
}
