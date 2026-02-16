package org.jetbrains.sbt

import sbt.*
import sbt.internal.util.{Terminal, LineReader}

object FullReaderCompat:
  def newFullReader(history: Option[File], state: State): FullReader =
    new FullReader(history, state.combinedParser, LineReader.HandleCONT, Terminal.console)
