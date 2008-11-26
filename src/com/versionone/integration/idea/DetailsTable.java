/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.util.ui.Table;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.TasksProperties;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.util.Vector;

public class DetailsTable extends Table {

    public DetailsTable(VerticalTableModel v1TableModel, IDataLayer data) {
        super(v1TableModel);
    }

    @Override
    public VerticalTableModel getModel() {
        return (VerticalTableModel) super.getModel();
    }

    public TableCellEditor getCellEditor(final int row, final int col) {
        if (col == 1 && getModel().getRowType(row) == TasksProperties.Type.List) {
            final Vector<String> values = getModel().getAvailableValuesAt(row, col);
            JComboBox comboEditor = new JComboBox(values);
            //select current value
            comboEditor.setSelectedItem(getValueAt(row, col));
            comboEditor.setBorder(null);
            return new DefaultCellEditor(comboEditor);
        } else {
            return super.getCellEditor(row, col);
        }
    }
}
