package com.github.skrtks.norminette.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import java.awt.Button
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

object NorminetteSettingsPanel : Configurable {
    var OPTION_KEY_NORMINETTE = ""
    private var modified = false
    private var textField = TextFieldWithBrowseButton(JTextField(30))
    private var button = Button("Detect installation")
    private val listener = NorminetteModifiedListener(this)
    private val buttonListener = NorminetteButtonListener()

    override fun createComponent(): JComponent {
        val panel = JPanel()
        panel.layout = FlowLayout(FlowLayout.LEFT, 5, 5)

        val desc = FileChooserDescriptorFactory.createSingleFileDescriptor()
        textField.textField.document.addDocumentListener(listener)
        textField.addBrowseFolderListener(
            "Norminette Executable",
            "Please select the Norminette executable",
            null,
            desc
        )
        textField.text = OPTION_KEY_NORMINETTE

        panel.add(JLabel("Norminette: "))
        panel.add(textField)

        button.addActionListener(buttonListener)
        panel.add(button)

        return panel
    }

    override fun isModified(): Boolean {
        return modified
    }

    override fun apply() {
        OPTION_KEY_NORMINETTE = textField.text
        modified = false
    }

    override fun reset() {
        if (OPTION_KEY_NORMINETTE.isEmpty()) {
            detectAndSetPath()
        }
        modified = false
    }

    fun detectAndSetPath() {
        OPTION_KEY_NORMINETTE = findExecutableOnPath() ?: ""
        textField.text = OPTION_KEY_NORMINETTE.ifEmpty { "No installation found" }
    }

    private fun findExecutableOnPath(): String? {
        for (dirname in System.getenv("PATH").split(File.pathSeparator)) {
            val file = File(dirname, "norminette")
            if (file.isFile && file.canExecute()) {
                return file.absolutePath
            }
        }
        return null
    }

    override fun disposeUIResources() {
        textField.textField.document.removeDocumentListener(listener)
        button.removeActionListener(buttonListener)
    }

    @Suppress("DialogTitleCapitalization")
    override fun getDisplayName(): String {
        return "norminette"
    }

    private class NorminetteModifiedListener(private val settingsPanel: NorminetteSettingsPanel) : DocumentListener {
        override fun insertUpdate(documentEvent: DocumentEvent) {
            settingsPanel.modified = true
        }

        override fun removeUpdate(documentEvent: DocumentEvent) {
            settingsPanel.modified = true
        }

        override fun changedUpdate(documentEvent: DocumentEvent) {
            settingsPanel.modified = true
        }
    }

    private class NorminetteButtonListener : ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            detectAndSetPath()
        }
    }
}