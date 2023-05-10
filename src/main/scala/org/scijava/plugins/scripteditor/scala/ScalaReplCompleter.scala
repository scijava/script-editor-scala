package org.scijava.plugins.scripteditor.scala

import org.fife.ui.autocomplete.{BasicCompletion, Completion, CompletionProvider}
import org.jline.reader.Candidate
import org.scijava.log.{LogLevel, LogService}

import java.io.{OutputStream, PrintStream, Writer}

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
