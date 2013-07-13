package com.versionone.integration.idea.editors;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionListener;
import java.awt.*;

/**
 * Create cell editor with dialog support
 */
abstract class DialogCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private JButton button;
    private JTextField textField;

    public DialogCellEditor(String buttonAction) {
        button = new JButton();
        button.setActionCommand(buttonAction);
        button.addActionListener(this);
        button.setMinimumSize(new Dimension(25, 25));
        button.setMaximumSize(new Dimension(25, 25));
        button.setText("...");
        button.setBorderPainted(false);

        textField = new JTextField();

        textField.setEditable(false);
        textField.setBorder(null);
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        String textValue = "";
        JComponent pane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        textValue = value == null ? "" : value.toString();

        textField.setText(textValue);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(textField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 0;

        pane.add(button, c);

        return pane;
    }
}
