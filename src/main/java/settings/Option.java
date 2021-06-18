package settings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.panels.VerticalLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import ui.JFilePicker;

/**
 * Created by HD on 2015/1/1.
 */
public class Option implements Configurable {
    public static final String OPTION_KEY_NORMINETTE = "norminette";
//    public static final String OPTION_KEY_NORMINETTE_OPTIONS = "norminetteOptions";
    private boolean modified = false;
    private JFilePicker jFilePickerNorminette;
//    private JTextField jTextNorminetteOptions;
    private final OptionModifiedListener listener = new OptionModifiedListener(this);

    @Nls
    @Override
    public String getDisplayName() {
        return "norminette";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel jPanel = new JPanel();

        VerticalLayout verticalLayout = new VerticalLayout(1, 2);
        jPanel.setLayout(verticalLayout);

        jFilePickerNorminette = new JFilePicker("Norminette path:", "...");
//        JLabel jLabelCpplintOptions = new JLabel("cpplint.py options:");
//        jTextNorminetteOptions = new JTextField("", 39);

        reset();

        jFilePickerNorminette.getTextField().getDocument().addDocumentListener(listener);
//        jTextNorminetteOptions.getDocument().addDocumentListener(listener);

        jPanel.add(jFilePickerNorminette);

        return jPanel;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        Settings.set(OPTION_KEY_NORMINETTE, jFilePickerNorminette.getTextField().getText());
//        Settings.set(OPTION_KEY_CPPLINT_OPTIONS, jTextNorminetteOptions.getText());
        modified = false;
    }

    @Override
    public void reset() {
        String norminette = Settings.get(OPTION_KEY_NORMINETTE);
        jFilePickerNorminette.getTextField().setText(norminette);

//        String norminetteOptions = Settings.get(OPTION_KEY_NORMINETTE_OPTIONS);
//        jTextNorminetteOptions.setText(norminetteOptions);
        modified = false;
    }

    @Override
    public void disposeUIResources() {
        jFilePickerNorminette.getTextField().getDocument().removeDocumentListener(listener);
//        jTextNorminetteOptions.getDocument().removeDocumentListener(listener);
    }

    private static class OptionModifiedListener implements DocumentListener {
        private final Option option;

        public OptionModifiedListener(Option option) {
            this.option = option;
        }

        @Override
        public void insertUpdate(DocumentEvent documentEvent) {
            option.setModified(true);
        }

        @Override
        public void removeUpdate(DocumentEvent documentEvent) {
            option.setModified(true);
        }

        @Override
        public void changedUpdate(DocumentEvent documentEvent) {
            option.setModified(true);
        }
    }
}
