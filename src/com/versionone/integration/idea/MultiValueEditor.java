package com.versionone.integration.idea;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.PropertyValues;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiValueEditor extends AbstractCellEditor implements TableCellEditor {
    private PropertyValues currentValue;
    private PropertyValues newValue;
    // TODO get entity type

    public Object getCellEditorValue() {
        return newValue != null ? newValue : currentValue;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentValue = (PropertyValues) value;
        return new MultiValueEditorDialog(null, "Edit", currentValue);
    }

    private class MultiValueEditorDialog extends JDialog {
        private final PropertyValues value;

        public MultiValueEditorDialog(JFrame parent, String title, PropertyValues value) {
            super(parent, title, true);
            this.value = value;
            setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);

            createControls();
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

        private void fillList(JList list) {
//            IDataLayer dataLayer = ApiDataLayer.getInstance();
//            PropertyValues allValues = dataLayer.getListPropertyValues(...)
        }
    }
}
