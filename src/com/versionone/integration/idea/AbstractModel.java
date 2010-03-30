/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.PropertyValues;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Abstract model for Details view
 */
public abstract class AbstractModel extends AbstractTableModel {

    protected final IDataLayer data;
    protected final Configuration configuration;

    public AbstractModel(IDataLayer data) {
        this.data = data;
        configuration = Configuration.getInstance();
    }

    protected abstract PropertyValues getAvailableValuesAt(int rowIndex, int columnIndex);

    public abstract String getColumnName(int column);

    public abstract boolean isCellEditable(int rowIndex, int columnIndex);

    protected abstract Configuration.ColumnSetting getRowSettings(int rowIndex);

    public abstract boolean isRowChanged(int row);

    public TableCellEditor getCellEditor(int row, int col) {
        Workitem item = getWorkitem();
        Configuration.ColumnSetting rowSettings = getRowSettings(row);

        if (rowTypeMatches(rowSettings, Configuration.AssetDetailSettings.STRING_TYPE, Configuration.AssetDetailSettings.EFFORT_TYPE)) {
            return createTextField(rowSettings.readOnly || item.isPropertyReadOnly(rowSettings.attribute));
        } else if (rowTypeMatches(rowSettings, Configuration.AssetDetailSettings.LIST_TYPE)) {
            final PropertyValues values = getAvailableValuesAt(row, col);
            final JComboBox comboEditor = new JComboBox(values.toArray());

            //select current value
            comboEditor.setSelectedItem(getValueAt(row, col));
            comboEditor.setBorder(null);
            return new DefaultCellEditor(comboEditor);
        }

        return createTextField(true);
    }

    private boolean rowTypeMatches(Configuration.ColumnSetting settings, String... types) {
        for(String type : types) {
            if(settings.type.equals(type)) {
                return true;
            }
        }

        return false;
    }

    private DefaultCellEditor createTextField(boolean isReadOnly) {
        // create text field for ID
        final JTextField textField = new JTextField();
        textField.setEditable(!isReadOnly);
        textField.setEnabled(true);
        textField.setFocusable(true);
        textField.setBorder(new LineBorder(Color.black));
        // popup menu with copy functionality
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Copy");
        menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textField.copy();
            }
        });
        menu.add(menuItem1);
        textField.add(menu);

        MouseListener popupListener = new ContextMenuMouseListener(menu);
        textField.addMouseListener(popupListener);

        return new DefaultCellEditor(textField);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (!getRowSettings(rowIndex).readOnly) {
            getWorkitem().setProperty(getRowSettings(rowIndex).attribute, aValue);

            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    protected abstract Workitem getWorkitem();
}