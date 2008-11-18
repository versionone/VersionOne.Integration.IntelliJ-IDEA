package com.versionone.integration.idea;

import static com.versionone.integration.idea.TasksProperties.DETAIL_ESTIMATE;
import static com.versionone.integration.idea.TasksProperties.DONE;
import static com.versionone.integration.idea.TasksProperties.EFFORT;
import static com.versionone.integration.idea.TasksProperties.ID;
import static com.versionone.integration.idea.TasksProperties.PARENT;
import static com.versionone.integration.idea.TasksProperties.STATUS;
import static com.versionone.integration.idea.TasksProperties.TITLE;
import static com.versionone.integration.idea.TasksProperties.TO_DO;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;

/**
 *
 */
public class HorizontalTableModel extends AbstractTableModel {

    private static final TasksProperties[] tasksColumnDataEffort =
            {TITLE, ID, PARENT, DETAIL_ESTIMATE, DONE, EFFORT, TO_DO, STATUS};
    private static final TasksProperties[] tasksColumnData =
            {TITLE, ID, PARENT, DETAIL_ESTIMATE, TO_DO, STATUS};

    private final DataLayer data;

    public HorizontalTableModel() {
        data = DataLayer.getInstance();
    }

    public int getRowCount() {
        if (data != null) {
            return data.getTasksCount();
        } else {
            return 0;
        }
    }

    public int getColumnCount() {
        return data.isTrackEffort() ? tasksColumnDataEffort.length : tasksColumnData.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return roundIfBigDecimal(data.getTaskPropertyValue(rowIndex, getColumnData(columnIndex)));
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
        return getColumnData(column).columnName;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return getColumnData(columnIndex).isEditable;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (getColumnData(columnIndex).type == TasksProperties.Type.Number) {
            try {
                aValue = roundIfBigDecimal(new BigDecimal((String) aValue));
            } catch (Exception e) {
                //We can popup error message there.
                return;
            }
        }
        data.setTaskPropertyValue(rowIndex, getColumnData(columnIndex), aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public boolean isRowChanged(int rowIndex) {
        return data.isTaskDataChanged(rowIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getColumnData(columnIndex).type.columnClass;
    }

    private TasksProperties getColumnData(int column) {
        return data.isTrackEffort() ? tasksColumnDataEffort[column] : tasksColumnData[column];
    }

    public TasksProperties.Type getColumnType(int column) {
        return getColumnData(column).type;
    }
}
