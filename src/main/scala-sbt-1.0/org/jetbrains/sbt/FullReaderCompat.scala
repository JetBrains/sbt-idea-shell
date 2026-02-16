package org.jetbrains.sbt

import sbt.*

object FullReaderCompat {
  def newFullReader(history: Option[File], state: State): FullReader =
    new FullReader(history, state.combinedParser)
}
