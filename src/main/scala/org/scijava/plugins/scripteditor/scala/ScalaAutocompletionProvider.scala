package org.scijava.plugins.scripteditor.scala

import org.fife.ui.autocomplete.{Completion, DefaultCompletionProvider}
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.scijava.log.LogService
import org.scijava.ui.swing.script.autocompletion.{ClassUtil, ImportFormat}

import javax.swing.text.{BadLocationException, JTextComponent}
import scala.jdk.CollectionConverters.*

class ScalaAutocompletionProvider(logService: LogService) extends DefaultCompletionProvider:

  private val completer: ScalaReplCompleter = new ScalaReplCompleter(logService)

  // for methods and functions
  this.setParameterizedCompletionParams('(', ", ", ')')
  // when using auto-activation, make it so that it occurs after any letter or '.'
  setAutoActivationRules(true, ".")
  // Setup cache ??
  new Thread(() => ClassUtil.ensureCache()).start()

  override def getCompletionsImpl(comp: JTextComponent): java.util.List[Completion] =
    val prefix = "ScalaAutocompletionProvider.getCompletionsImpl - "

    val completions         = new java.util.ArrayList[Completion]
    var currentLine         = ""
    var codeWithoutLastLine = ""
    val alreadyEnteredText  = this.getAlreadyEnteredText(comp)

    try
      codeWithoutLastLine = comp.getText(0, comp.getCaretPosition)
      val lastLineBreak = codeWithoutLastLine.lastIndexOf("\n") + 1
      currentLine = codeWithoutLastLine.substring(lastLineBreak) // up to the caret position

      codeWithoutLastLine = codeWithoutLastLine.substring(0, lastLineBreak)
    catch
      case e1: BadLocationException =>
        logService.trace(prefix + "code text extraction", e1)
        return completions

    // Completions
    try
      val cs =
        completer.completionsFor(this, codeWithoutLastLine, currentLine, alreadyEnteredText)

      completions.addAll(cs.asJava)
    catch
      case e: Exception =>
        logService.trace(prefix + "Failed to get auto-completions.")
        logService.trace(prefix, e)

    completions

  end getCompletionsImpl

end ScalaAutocompletionProvider
