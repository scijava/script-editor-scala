package org.scijava.plugins.scripteditor.scala

import dotty.tools.repl.{ReplDriver, State}
import org.jline.reader.Candidate

import java.io.PrintStream

/**
 * Utilize the Scala's ReplDriver as a auto-completion provider
 */
class AutoCompleteReplDriver(
  settings: Array[String],
  out: PrintStream = Console.out,
  classLoader: Option[ClassLoader] = None
) extends ReplDriver(settings, out, classLoader):

  /**
   * Extract possible completions at the index of `cursor` in `expr`
   */
  def complete(cursor: Int, expr: String, state0: State): List[Candidate] =
    completions(cursor, expr, state0)
