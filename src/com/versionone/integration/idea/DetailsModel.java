/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.TasksProperties;
import static com.versionone.common.sdk.TasksProperties.BUILD;
import static com.versionone.common.sdk.TasksProperties.DESCRIPTION;
import static com.versionone.common.sdk.TasksProperties.DETAIL_ESTIMATE;
import static com.versionone.common.sdk.TasksProperties.DONE;
import static com.versionone.common.sdk.TasksProperties.EFFORT;
import static com.versionone.common.sdk.TasksProperties.OWNER;
import static com.versionone.common.sdk.TasksProperties.PARENT;
import static com.versionone.common.sdk.TasksProperties.PROJECT;
import static com.versionone.common.sdk.TasksProperties.REFERENCE;
import static com.versionone.common.sdk.TasksProperties.SOURCE;
import static com.versionone.common.sdk.TasksProperties.SPRINT;
import static com.versionone.common.sdk.TasksProperties.STATUS;
import static com.versionone.common.sdk.TasksProperties.TITLE;
import static com.versionone.common.sdk.TasksProperties.TO_DO;
import static com.versionone.common.sdk.TasksProperties.TYPE;

import java.util.Vector;

public class DetailsModel extends AbstractModel {

    private static final TasksProperties[] propertiesWithEffort = {BUILD, DESCRIPTION, DETAIL_ESTIMATE, DONE, EFFORT,
            OWNER, PARENT, PROJECT, REFERENCE, SOURCE, SPRINT, STATUS, TITLE, TO_DO, TYPE};
    private static final TasksProperties[] properties = {BUILD, DESCRIPTION, DETAIL_ESTIMATE,
            OWNER, PARENT, PROJECT, REFERENCE, SOURCE, SPRINT, STATUS, TITLE, TO_DO, TYPE};
    private static String[] columnsNames = {"Property", "Value"};

    private int task = Integer.MAX_VALUE;

    public DetailsModel(IDataLayer data) {
        super(data);
    }

    public void setTask(int task) {
        if (task >= 0) {
            this.task = task;
        } else {
            this.task = Integer.MAX_VALUE;
        }
    }

    public boolean isTaskSet() {
        return task < data.getTasksCount();
    }

    public int getRowCount() {
        return getPropertiesCount();
    }

    public int getColumnCount() {
        return 2;
    }

    public Vector<String> getAvailableValuesAt(int rowIndex, int columnIndex) {
        if (columnIndex == 1 && isTaskSet()) {
            return data.getPropertyValues(getProperty(rowIndex, columnIndex));
        }
        return null;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return getProperty(rowIndex, columnIndex).columnName;
        }
        if (isTaskSet()) {
            return data.getTaskPropertyValue(task, getProperty(rowIndex, columnIndex));
        }
        return null;
    }

    public String getColumnName(int column) {
        return columnsNames[column];
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (isTaskSet()) {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    @Override
    protected int getTask(int rowIndex, int columnIndex) {
        return task;
    }

    public boolean isRowChanged(int rowIndex) {
        boolean result = false;
        if (isTaskSet()) {
            result = data.isPropertyChanged(task, getProperty(rowIndex, -1));
        }
        return result;
    }

    protected TasksProperties getProperty(int rowIndex, int columnIndex) {
        return data.isTrackEffort() ? propertiesWithEffort[rowIndex] : properties[rowIndex];
    }

    public int getPropertiesCount() {
        return data.isTrackEffort() ? propertiesWithEffort.length : properties.length;
    }
}