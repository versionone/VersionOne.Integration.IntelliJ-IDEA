package com.versionone.integration.idea;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.math.BigInteger;

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
        return convertValueToString(data.getTaskPropertyValue(rowIndex, columnData[columnIndex]));
    }

    private static String convertValueToString(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Double) {
            long i100 = Math.round((Double) value * 100);
            return new BigDecimal(BigInteger.valueOf(i100), 2).toPlainString();
        } else {
            return value.toString();
        }
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
//        columnData[columnIndex].type.isValue
        data.setTaskPropertyValue(rowIndex, columnData[columnIndex], aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public boolean isRowChanged(int rowIndex) {
        return data.isTaskDataChanged(rowIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnData[columnIndex].type.columnClass;
    }
}
