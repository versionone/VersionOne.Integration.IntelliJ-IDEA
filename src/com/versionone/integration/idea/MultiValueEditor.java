package com.versionone.integration.idea;

import com.versionone.common.sdk.PropertyValues;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiValueEditor extends AbstractCellEditor implements TableCellEditor {

    public Object getCellEditorValue() {
        // return cell value
        return null;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // return created component
        return null;
    }

    private class MultiValueEditorDialog extends JDialog {
        private final PropertyValues value;

        public MultiValueEditorDialog(JFrame parent, String title, PropertyValues value) {
            super(parent, title, true);
            this.value = value;
            setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);

            createControls();
        }

        public PropertyValues getValue() {
            return value;
        }

        private void createControls() {
            final JList list = new JList(value.toArray());
            add(list);

            JButton okButton = new JButton("OK");
            add(okButton);
            JButton cancelButton = new JButton("Cancel");
            add(cancelButton);

            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // TODO accept changes in list
                    MultiValueEditorDialog.this.setVisible(false);
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MultiValueEditorDialog.this.setVisible(false);
                }
            });
        }
    }
}
