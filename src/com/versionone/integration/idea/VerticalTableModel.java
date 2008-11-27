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

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.util.Vector;

/**
 *
 */
public class VerticalTableModel extends AbstractTableModel {

    private static final TasksProperties[] tasksRowDataEffort = {BUILD, DESCRIPTION, DETAIL_ESTIMATE, DONE, EFFORT,
            OWNER, PARENT, PROJECT, REFERENCE, SOURCE, SPRINT, STATUS, TITLE, TO_DO, TYPE};
    private static final TasksProperties[] tasksRowData = {BUILD, DESCRIPTION, DETAIL_ESTIMATE,
            OWNER, PARENT, PROJECT, REFERENCE, SOURCE, SPRINT, STATUS, TITLE, TO_DO, TYPE};

    private final IDataLayer data;
    private int task = 0;//TODO

    public VerticalTableModel(IDataLayer data) {
        this.data = data;
    }

    public int getRowCount() {
        return data.isTrackEffort() ? tasksRowDataEffort.length : tasksRowData.length;
    }

    public int getColumnCount() {
        return 2;
    }

    public Vector<String> getAvailableValuesAt(int rowIndex, int columnIndex) {
        if (columnIndex != 0 && isTaskSet()) {
            return data.getAvailableValues(task, getRowData(rowIndex));
        }
        return null;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object res = null;
        if (columnIndex == 0) {
            res = getRowData(rowIndex).columnName;
        } else if (isTaskSet()) {
            res = data.getTaskPropertyValue(task, getRowData(rowIndex));
        }
        return res;
    }

    public void setTask(int task) {
        this.task = task;
    }

    private boolean isTaskSet() {
        return task >= 0;
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "Property" : "Value";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0 || !isTaskSet()) {
            return false;
        }
        return getRowData(rowIndex).isEditable;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data.setTaskPropertyValue(task, getRowData(rowIndex), (String) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

/*
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getColumnData(columnIndex).type.columnClass;
    }
*/

    private TasksProperties getRowData(int row) {
        return data.isTrackEffort() ? tasksRowDataEffort[row] : tasksRowData[row];
    }

    public TasksProperties.Type getRowType(int row) {
        return getRowData(row).type;
    }

    public boolean isRowChanged(int row) {
        return data.isPropertyChanged(task, getRowData(row));
    }

    public TableCellEditor getCellEditor(int row, int col) {
        if (col != 1 || getRowType(row) != TasksProperties.Type.List) {
            return null;
        }
        final Vector<String> values = getAvailableValuesAt(row, col);
        final JComboBox comboEditor = new JComboBox(values);

        //select current value
        comboEditor.setSelectedItem(getValueAt(row, col));
        comboEditor.setBorder(null);
        return new DefaultCellEditor(comboEditor);
    }
}