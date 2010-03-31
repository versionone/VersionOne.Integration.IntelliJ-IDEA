/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.PropertyValues;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

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
            boolean isReadOnly = rowSettings.readOnly || item.isPropertyReadOnly(rowSettings.attribute);
            return EditorFactory.createTextFieldEditor(!isReadOnly);
        } else if (rowTypeMatches(rowSettings, Configuration.AssetDetailSettings.LIST_TYPE)) {
            return EditorFactory.createComboBoxEditor(item, rowSettings.attribute, getValueAt(row, col));
        }

        return EditorFactory.createTextFieldEditor(false);
    }

    private boolean rowTypeMatches(Configuration.ColumnSetting settings, String... types) {
        for(String type : types) {
            if(settings.type.equals(type)) {
                return true;
            }
        }

        return false;
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