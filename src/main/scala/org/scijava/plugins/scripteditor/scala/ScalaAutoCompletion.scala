package org.scijava.plugins.scripteditor.scala

import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.autocomplete.CompletionProvider

/**
 * @param provider The completion provider. This cannot be `null`
 */
class ScalaAutoCompletion(provider: CompletionProvider) extends AutoCompletion(provider):
  this.setParameterAssistanceEnabled(true)
  this.setShowDescWindow(false)
