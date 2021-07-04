package com.samkortekaas.norminette.settings

import com.intellij.ide.util.PropertiesComponent
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
    private var modified = false
    private var textField = TextFieldWithBrowseButton(JTextField(30))
    private var button = Button("Detect installation")
    private val listener = NorminetteModifiedListener(this)
    private val buttonListener = NorminetteButtonListener()
    private val persistentProperties = PropertiesComponent.getInstance()
    private const val pathKey = "skrtks.norminette.settings.path"
    var NORMINETTE_PATH_VAL = persistentProperties.getValue(pathKey) ?: ""

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
        textField.text = NORMINETTE_PATH_VAL

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
        NORMINETTE_PATH_VAL = textField.text
        persistentProperties.setValue(pathKey, NORMINETTE_PATH_VAL)
        modified = false
    }

    override fun reset() {
        if (NORMINETTE_PATH_VAL.isEmpty()) {
            detectAndSetPath()
        }
        modified = false
    }

    fun detectAndSetPath() {
        NORMINETTE_PATH_VAL = findExecutableOnPath() ?: ""
        persistentProperties.setValue(pathKey, NORMINETTE_PATH_VAL)
        textField.text = NORMINETTE_PATH_VAL.ifEmpty { "No installation found" }
    }

    private fun findExecutableOnPath(): String? {
        val path = System.getenv("PATH") + ":/usr/local/bin" // /usr/local/bin not in PATH for applications by default on macOS
        for (dirname in path.split(File.pathSeparator)) {
            val file = File(dirname, "norminette")
            if (file.exists() && file.canExecute()) {
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
            modified = true
        }

        override fun removeUpdate(documentEvent: DocumentEvent) {
            modified = true
        }

        override fun changedUpdate(documentEvent: DocumentEvent) {
            modified = true
        }
    }

    private class NorminetteButtonListener : ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            detectAndSetPath()
        }
    }
}