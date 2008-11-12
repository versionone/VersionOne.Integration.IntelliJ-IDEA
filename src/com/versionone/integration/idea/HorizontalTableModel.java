package com.versionone.integration.idea;

import javax.swing.table.AbstractTableModel;

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
        switch (columnData[columnIndex].getType()) {
            case Text:
                break;
        }
        data.setTaskPropertyValue(rowIndex, columnData[columnIndex], aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public boolean isRowChanged(int rowIndex) {
        return data.isTaskDataChanged(rowIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnData[columnIndex].getType().getColumnClass();
    }
}
