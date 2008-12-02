/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.TasksProperties;
import static com.versionone.common.sdk.TasksProperties.DETAIL_ESTIMATE;
import static com.versionone.common.sdk.TasksProperties.DONE;
import static com.versionone.common.sdk.TasksProperties.EFFORT;
import static com.versionone.common.sdk.TasksProperties.ID;
import static com.versionone.common.sdk.TasksProperties.PARENT;
import static com.versionone.common.sdk.TasksProperties.STATUS;
import static com.versionone.common.sdk.TasksProperties.TITLE;
import static com.versionone.common.sdk.TasksProperties.TO_DO;

import java.util.Vector;

public class NewTaskModel extends AbstractModel {

    private static final TasksProperties[] tasksColumnDataEffort =
            {TITLE, ID, PARENT, DETAIL_ESTIMATE, DONE, EFFORT, TO_DO, STATUS};
    private static final TasksProperties[] tasksColumnData =
            {TITLE, ID, PARENT, DETAIL_ESTIMATE, TO_DO, STATUS};

    public NewTaskModel(IDataLayer data) {
        super(data, tasksColumnData, tasksColumnDataEffort);
    }

    public Vector<String> getAvailableValuesAt(int rowIndex, int columnIndex) {
        return getProperty(columnIndex).getListValues();
    }

    public int getRowCount() {
        return data.getTasksCount();
    }

    public int getColumnCount() {
        return getPropertiesCount();
    }

    public String getColumnName(int column) {
        return getProperty(column).columnName;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return getProperty(columnIndex).isEditable;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.getTaskPropertyValue(rowIndex, getProperty(columnIndex));
    }

    public boolean isRowChanged(int row) {
        return data.isTaskDataChanged(row);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data.setTaskPropertyValue(rowIndex, getProperty(columnIndex), (String) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
