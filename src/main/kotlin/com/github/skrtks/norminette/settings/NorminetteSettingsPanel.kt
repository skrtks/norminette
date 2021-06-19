package com.github.skrtks.norminette.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

object NorminetteSettingsPanel : Configurable {
    var OPTION_KEY_NORMINETTE = "norminette"
    private var modified = false
    private val listener = NorminetteModifiedListener(this)
    private var textField: TextFieldWithBrowseButton? = null

    override fun createComponent(): JComponent {
        val panel = JPanel()
        panel.layout = FlowLayout(FlowLayout.LEFT, 5, 5)

        val descr = FileChooserDescriptorFactory.createSingleFileDescriptor()
        textField = TextFieldWithBrowseButton(JTextField(30))
        textField!!.textField.document.addDocumentListener(listener)
        textField!!.addBrowseFolderListener("Norminette executable", "Please select the Norminette executable", null, descr)
        panel.add(JLabel("Norminette: "))
        panel.add(textField)

        return panel

    }

    override fun isModified(): Boolean {
        return modified
    }

    override fun apply() {
        OPTION_KEY_NORMINETTE = textField!!.text
        modified = false
    }

    override fun reset() {
        textField!!.textField.text = OPTION_KEY_NORMINETTE
        modified = false
    }

    override fun disposeUIResources() {
        textField!!.textField.document.removeDocumentListener(listener)
    }

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

//    myCMake = createActionItemsComboBox(myCMakeModel)
//    cMakeLabel.labelFor = myCMake.comboBox
//    add(myCMake, bag.next().coverLine().insetTop(nextTopInset))
//
//    val cmakeToolsCheckLabel = HyperlinkLabel()
//    cmakeToolsCheckLabel.setFontSize(UIUtil.FontSize.SMALL)
//
//    val cmakeToolsChecker = addCMakeToolsChecker(cmakeToolsCheckLabel)
//    addVersionChecker(
//    myCMake,
//    bag.nextLine().next().next(),
//    object : VersionChecker<CPPToolchainsPanel.SelectedCMake>(true) {
//        override val selectedItem: CPPToolchainsPanel.SelectedCMake?
//            get() = selectedCMake
//
//        override fun readAndCheckVersion(
//            selectedToolSet: CPPToolSet?,
//            selectedItem: CPPToolchainsPanel.SelectedCMake?
//        ): CheckedVersion {
//            if (selectedItem == null) return CheckedVersion("CMake")
//
//            val toolchain = createTemplateToolchain()
//            apply(toolchain)
//            val version = CMakeExecutableTool.readCMakeVersion(toolchain)
//            return CheckedVersion(
//                "CMake",
//                version, warning = version?.let { CMakeExecutableTool.checkVersion(it) },
//                isBundled = selectedItem.isBundled,
//                isFromToolSet = selectedToolSet.isToolSetCMake(selectedItem.path?.let(java.io::File))
//            )
//        }
//    }
//    )
//    { cmakeToolsChecker.run() }
}