package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiValueEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private PropertyValues currentValue;

    private final IDataLayer dataLayer;
    private final Workitem item;
    private final String propertyName;

    private JButton button;
    private MultiValueEditorDialog dialog;


    public MultiValueEditor(@NotNull IDataLayer dataLayer, @NotNull Workitem item, @NotNull String propertyName, @NotNull JTable table) {
        this.dataLayer = dataLayer;
        this.item = item;
        this.propertyName = propertyName;

        button = new JButton();
        button.setActionCommand("edit");
        button.addActionListener(this);
        JFrame parent = (JFrame) SwingUtilities.getRoot(table);
        dialog = new MultiValueEditorDialog(parent, "Edit", table);
    }

    public void actionPerformed(ActionEvent e) {
        if ("edit".equals(e.getActionCommand())) {
            button.setSize(10, 10);
            dialog.setVisible(true);

            fireEditingStopped(); 
        }
    }

    public Object getCellEditorValue() {
        return currentValue == null ? item.getProperty(propertyName) : currentValue;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentValue = (PropertyValues) value;
        return button;
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
