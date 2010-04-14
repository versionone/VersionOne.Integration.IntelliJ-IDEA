package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiValueEditor extends AbstractCellEditor implements TableCellEditor {

    private PropertyValues currentValue;

    private final IDataLayer dataLayer;
    private final Workitem item;
    private final String propertyName;

    public MultiValueEditor(@NotNull IDataLayer dataLayer, @NotNull Workitem item, @NotNull String propertyName) {
        this.dataLayer = dataLayer;
        this.item = item;
        this.propertyName = propertyName;
    }

    public Object getCellEditorValue() {
        return currentValue == null ? item.getProperty(propertyName) : currentValue;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentValue = (PropertyValues) value;
        JFrame parent = (JFrame) SwingUtilities.getRoot(table);
        MultiValueEditorDialog dialog = new MultiValueEditorDialog(parent, "Edit", table);
        dialog.setVisible(true);
        return dialog;
    }

    private class MultiValueEditorDialog extends JDialog {

        private JList valuesList;
        private final JTable table;

        public MultiValueEditorDialog(JFrame parent, String title, JTable table) {
            super(parent, title, true);
            this.table = table;

            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setSize(200, 200);
            setLayout(new BorderLayout());

            createControls();
        }

        private void createControls() {
            createAndBindList();
            JScrollPane valuesScrollPane = new JScrollPane(valuesList);
            add(valuesScrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton okButton = new JButton("OK");
            buttonPanel.add(okButton);
            JButton cancelButton = new JButton("Cancel");
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.PAGE_END);

            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    currentValue = new PropertyValues();
                    Object[] values = valuesList.getSelectedValues();

                    for(Object value : values) {
                        currentValue.add((ValueId) value);
                    }
                    
                    setVisible(false);
                    fireEditingStopped();
                    table.editingStopped(new ChangeEvent(MultiValueEditor.this));
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    fireEditingCanceled();
                }
            });

            centerDialog();
        }

        private void createAndBindList() {
            PropertyValues allValues = dataLayer.getListPropertyValues(item.getType(), propertyName);
            PropertyValues currentValues = (PropertyValues) item.getProperty(propertyName);

            ValueId[] allValuesArray = allValues.toArray();
            valuesList = new JList(allValuesArray);

            ValueId[] currentValuesArray = currentValues.toArray();
            int[] selectedIndices = new int[currentValuesArray.length];

            for(int i = 0; i < selectedIndices.length; i++) {
                selectedIndices[i] = getValueIdIndex(currentValuesArray[i], allValuesArray);
            }

            valuesList.setSelectedIndices(selectedIndices);    
        }

        private int getValueIdIndex(ValueId value, ValueId[] allValues) {
            for(int i = 0; i < allValues.length; i++) {
                if(value.equals(allValues[i])) {
                    return i;
                }
            }

            return -1;
        }
        
        private void centerDialog() {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height)/ 2);
        }
    }
}
