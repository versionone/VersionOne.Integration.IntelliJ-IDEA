package com.versionone.integration.idea;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;

/**
 *
 */
public class HorizontalTableModel extends AbstractTableModel {

    private final TasksProperties[] columnData;
    private final DataLayer data;

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
        return roundIfBigDecimal(data.getTaskPropertyValue(rowIndex, columnData[columnIndex]));
    }

    private static Object roundIfBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            BigDecimal b = (BigDecimal) value;
            return b.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return value;
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
        if (columnData[columnIndex].type == TasksProperties.Type.Number) {
            try {
                aValue = roundIfBigDecimal(new BigDecimal((String) aValue));
            } catch (Exception e) {
                //We can popup error message there.
                return;
            }
        }
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
