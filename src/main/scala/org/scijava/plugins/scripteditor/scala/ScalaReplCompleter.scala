/*-
 * #%L
 * Scala language support for SciJava Script Editor.
 * %%
 * Copyright (C) 2020 - 2023 SciJava developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.scijava.plugins.scripteditor.scala

import org.fife.ui.autocomplete.{BasicCompletion, Completion, CompletionProvider}
import org.jline.reader.Candidate
import org.scijava.log.{LogLevel, LogService}

import java.io.{OutputStream, PrintStream, Writer}

/**
 * @author Jarek Sacha
 */
object ScalaReplCompleter:

  private final class LevelOutputStream(logService: LogService) extends OutputStream:

    // TODO: redirect logging to the logService rather than standard output

    private val writer   = Console.out
    private val logLevel = LogLevel.TRACE

    override def write(b: Int): Unit =
      if logService.isLevel(logLevel) then
        writer.write(Array(b.toByte), 0, 1)

    override def write(b: Array[Byte], off: Int, len: Int): Unit =
      if logService.isLevel(logLevel) then
        writer.write(b, off, len)

end ScalaReplCompleter

/**
 * @author Jarek Sacha
 */
class ScalaReplCompleter(logService: LogService):
  import ScalaReplCompleter.LevelOutputStream

  // Auto-completion may be parsing incorrect code, no need to print parsing errors, unless `Trace` debug level is set
  private val out    = new PrintStream(new LevelOutputStream(logService))
  private val loader = Some(getClass.getClassLoader)

  private val driver =
    new AutoCompleteReplDriver(
      Array(
        //        "-classpath", "", // Avoid the default "."
        "-usejavacp",
        "-color:never",
        "-Xrepl-disable-display",
        "-explain"
      ),
      out,
      loader
    )

  private val state = driver.initialState

  def completionsFor(
    provider: CompletionProvider,
    codeWithoutLastLine: String,
    lastLine: String,
    alreadyEnteredText: String
  ): Seq[Completion] =

    logService.trace(
      s"""|ScalaReplCompleter.completionsFor(
         |  codeWithoutLastLine: 
         |    $codeWithoutLastLine
         |  lastLine: $lastLine
         |  alreadyEnteredText:  $alreadyEnteredText
         |)
         |""".stripMargin
    )

    // Attempt to parse code already entered
    val state1 = driver.run(codeWithoutLastLine)(using state)

    // Get completion for code on the last line, with context from code already entered (state1)
    val candidates = driver.complete(lastLine.length, lastLine, state1)

    // Convert to format expected by the Script Editor
    candidates
      .map((c: Candidate) => new BasicCompletion(provider, c.value()))

end ScalaReplCompleter
