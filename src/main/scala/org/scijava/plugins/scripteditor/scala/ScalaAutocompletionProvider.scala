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

import org.fife.ui.autocomplete.{Completion, DefaultCompletionProvider}
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.scijava.log.LogService
import org.scijava.ui.swing.script.autocompletion.{ClassUtil, ImportFormat}

import javax.swing.text.{BadLocationException, JTextComponent}
import scala.jdk.CollectionConverters.*

/**
 * @author Jarek Sacha
 */
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
