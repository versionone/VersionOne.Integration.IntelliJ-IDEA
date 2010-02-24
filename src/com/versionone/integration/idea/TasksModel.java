/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.oldsdk.IDataLayer;
import com.versionone.common.oldsdk.TasksProperties;
import static com.versionone.common.oldsdk.TasksProperties.DETAIL_ESTIMATE;
import static com.versionone.common.oldsdk.TasksProperties.DONE;
import static com.versionone.common.oldsdk.TasksProperties.EFFORT;
import static com.versionone.common.oldsdk.TasksProperties.ID;
import static com.versionone.common.oldsdk.TasksProperties.PARENT;
import static com.versionone.common.oldsdk.TasksProperties.STATUS;
import static com.versionone.common.oldsdk.TasksProperties.TITLE;
import static com.versionone.common.oldsdk.TasksProperties.TO_DO;

import java.util.Vector;

public class TasksModel extends AbstractModel {

    private static final TasksProperties[] propertiesWithEffort =
            {TITLE, ID, PARENT, DETAIL_ESTIMATE, DONE, EFFORT, TO_DO, STATUS};
    private static final TasksProperties[] properties =
            {TITLE, ID, PARENT, DETAIL_ESTIMATE, TO_DO, STATUS};

    public TasksModel(IDataLayer data) {
        super(data);
    }

    public Vector<String> getAvailableValuesAt(int rowIndex, int columnIndex) {
        return data.getPropertyValues(getProperty(rowIndex, columnIndex));
    }

    public int getRowCount() {
        return data.getTasksCount();
    }

    public int getColumnCount() {
        return getPropertiesCount();
    }

    public String getColumnName(int columnIndex) {
        return getProperty(-1, columnIndex).columnName;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.getTaskPropertyValue(rowIndex, getProperty(rowIndex, columnIndex));
    }

    public boolean isRowChanged(int row) {
        return data.isTaskDataChanged(row);
    }

    @Override
    protected int getTask(int rowIndex, int columnIndex) {
        return rowIndex;
    }

    protected TasksProperties getProperty(int rowIndex, int columnIndex) {
        return data.isTrackEffort() ? propertiesWithEffort[columnIndex] : properties[columnIndex];
    }

    public int getPropertiesCount() {
        return data.isTrackEffort() ? propertiesWithEffort.length : properties.length;
    }
}
