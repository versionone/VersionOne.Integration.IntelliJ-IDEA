package com.versionone.integration.idea;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.util.EventObject;
import java.util.Collection;
import java.awt.*;

/**
 *
*/
public class HorizontalTableModel extends AbstractTableModel {

    private TasksProperties[] columnData = null;
    private DataLayer data;

    public HorizontalTableModel(TasksProperties[] columnData) {
        this.columnData = columnData;
        data = DataLayer.getInstance();
    }

    public int getRowCount() {
        return data.getTasksCount();
    }

    public int getColumnCount() {
        return columnData.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.getTaskPropertyValue(rowIndex, columnData[columnIndex]);
    }

    public TasksProperties getColumn(int column) {
         return columnData.length < column ? columnData[column] : null;
    }

    @Override
    public String getColumnName(int column) {
        return columnData[column].columnName;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnData[columnIndex].isEditable;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data.setTaskPropertyValue(rowIndex, columnData[columnIndex], aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
