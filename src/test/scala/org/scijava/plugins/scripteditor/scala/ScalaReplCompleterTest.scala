package org.scijava.plugins.scripteditor.scala

import org.fife.ui.autocomplete.Completion
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsCollectionContaining.hasItems
import org.junit.Test
import org.scijava.log.{LogLevel, LogService, StderrLogService}

import scala.jdk.CollectionConverters.*

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
