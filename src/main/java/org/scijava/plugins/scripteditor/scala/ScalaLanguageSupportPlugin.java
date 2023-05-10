package org.scijava.plugins.scripteditor.scala;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.scijava.Priority;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.script.LanguageSupportPlugin;
import org.scijava.ui.swing.script.LanguageSupportService;

/**
 * {@link LanguageSupportPlugin} for the Scala language.
 *
 * @author Jarek Sacha
 * @see LanguageSupportService
 */
@Plugin(type = LanguageSupportPlugin.class, priority = Priority.HIGH)
public class ScalaLanguageSupportPlugin extends AbstractLanguageSupport implements LanguageSupportPlugin {

    @Parameter
    private LogService log;

    private AutoCompletion ac;
    private RSyntaxTextArea text_area;

    public ScalaLanguageSupportPlugin() {
        setAutoCompleteEnabled(true);
        setParameterAssistanceEnabled(true);
        setShowDescWindow(false);
    }

    @Override
    public String getLanguageName() {
        return "scala";
    }

    @Override
    public void install(RSyntaxTextArea textArea) {
        this.text_area = textArea;
        this.ac = this.createAutoCompletion(null);
        this.ac.setParameterAssistanceEnabled(false);
        this.ac.setShowDescWindow(false);
        this.ac.install(textArea);
        // store upstream
        super.installImpl(textArea, this.ac);
    }

    @Override
    public void uninstall(RSyntaxTextArea textArea) {
        if (textArea == this.text_area) {
            super.uninstallImpl(textArea); // will call this.acp.uninstall();
        }
    }

    /**
     * Ignores the argument.
     */
    @Override
    protected AutoCompletion createAutoCompletion(CompletionProvider p) {
        return new ScalaAutoCompletion(new ScalaAutocompletionProvider(log));
    }
}
