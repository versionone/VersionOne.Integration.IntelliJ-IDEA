/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.TasksProperties;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.util.Vector;

/**
 *
 */
public abstract class AbstractModel extends AbstractTableModel {

    protected final IDataLayer data;
    private final TasksProperties[] properties;
    private final TasksProperties[] propertiesWithEffort;

    public AbstractModel(IDataLayer data, TasksProperties[] properties, TasksProperties[] propertiesWithEffort) {
        this.properties = properties;
        this.propertiesWithEffort = propertiesWithEffort;
        this.data = data;
    }

    public int getPropertiesCount() {
        return data.isTrackEffort() ? propertiesWithEffort.length : properties.length;
    }

    public abstract Vector<String> getAvailableValuesAt(int rowIndex, int columnIndex);

    public abstract String getColumnName(int column);

    public abstract boolean isCellEditable(int rowIndex, int columnIndex);

    protected TasksProperties getProperty(int index) {
        return data.isTrackEffort() ? propertiesWithEffort[index] : properties[index];
    }

    public abstract boolean isRowChanged(int row);

    public TableCellEditor getCellEditor(int row, int col) {
        final Vector<String> values = getAvailableValuesAt(row, col);
        if (values == null) {
            return null;
        }
        final JComboBox comboEditor = new JComboBox(values);

        //select current value
        comboEditor.setSelectedItem(getValueAt(row, col));
        comboEditor.setBorder(null);
        return new DefaultCellEditor(comboEditor);
    }
}