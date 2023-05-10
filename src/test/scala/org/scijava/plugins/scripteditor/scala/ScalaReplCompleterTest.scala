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

import org.fife.ui.autocomplete.Completion
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsCollectionContaining.hasItems
import org.junit.Test
import org.scijava.log.{LogLevel, LogService, StderrLogService}

import scala.jdk.CollectionConverters.*

/**
 * @author Jarek Sacha
 */
class ScalaReplCompleterTest:
  import ScalaReplCompleterTest.*

  @Test
  def importCompletion1(): Unit = test(
    codeWithoutLastLine = "",
    lastLine = "import ja",
    alreadyEnteredText = "import ja",
    expectedCompletions = Seq("java", "javax")
  )

  @Test
  def importCompletion2(): Unit = test(
    codeWithoutLastLine = "",
    lastLine = "import java.",
    alreadyEnteredText = "",
    expectedCompletions = Seq("beans", "math", "util")
  )

  @Test
  def declaredValueName(): Unit = test(
    codeWithoutLastLine = "val aaxx = 3.14",
    lastLine = "aa",
    alreadyEnteredText = "aa",
    expectedCompletions = Seq("aaxx")
  )

  @Test
  def predefined(): Unit = test(
    codeWithoutLastLine = "val aaxx = 3.14",
    lastLine = "Con",
    alreadyEnteredText = "Con",
    expectedCompletions = Seq("Console")
  )

  @Test
  def partialCode(): Unit = test(
    codeWithoutLastLine =
      """
        |val a = 1
        |
        |if (a == 2) {
        |
        |""".stripMargin,
    lastLine = "Con",
    alreadyEnteredText = "Con",
    expectedCompletions = Seq("Console")
  )

  private def test(
    codeWithoutLastLine: String,
    lastLine: String,
    alreadyEnteredText: String,
    expectedCompletions: Seq[String]
  ): Unit =
    val src         = new ScalaReplCompleter(logService)
    val completions = src.completionsFor(null, codeWithoutLastLine, lastLine, alreadyEnteredText)
    assertThat(
      completions.map(_.getReplacementText).asJava,
      hasItems(expectedCompletions*)
    )

end ScalaReplCompleterTest

object ScalaReplCompleterTest:
  val logService = new StderrLogService()
  logService.setLevel(LogLevel.NONE)
