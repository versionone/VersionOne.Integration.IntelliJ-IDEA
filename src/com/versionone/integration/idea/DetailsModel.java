/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ApiDataLayer;

import java.util.ArrayList;
import java.util.List;

public class DetailsModel extends AbstractModel {

    private static String[] columnsNames = {"Property", "Value"};
    /*
     * Info about data for workitem (lazy)
     */
    private Configuration.ColumnSetting[] workitemData;
    private Workitem workitem;

    public DetailsModel(IDataLayer data) {
        super(data);
    }

    public void setWorkitem(Workitem workitem) {
        if (this.workitem == null || workitem == null || !workitem.getType().equals(this.workitem.getType())) {
            workitemData = null;
        }
        this.workitem = workitem;
    }

    public boolean isWorkitemSet() {
        return workitem != null;
    }

    public int getRowCount() {
        if (isWorkitemSet()) {
            return getPropertiesCount();
        }
        return 0;
    }

    public int getColumnCount() {
        return 2;
    }

    protected PropertyValues getAvailableValuesAt(int rowIndex, int columnIndex) {
        Configuration.ColumnSetting column = getRowSettings(rowIndex);
        if (columnIndex == 1 && isWorkitemSet()) {
            return ApiDataLayer.getInstance().getListPropertyValues(getWorkitem().getType(), column.attribute);
        }
        return null;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return dataLayer.localizerResolve(getRowSettings(rowIndex).name);
        }
        if (isWorkitemSet()) {
            return workitem.getProperty(getRowSettings(rowIndex).attribute);
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
        if (isWorkitemSet() && !workitem.isPropertyReadOnly(getRowSettings(rowIndex).attribute) && !getRowSettings(rowIndex).readOnly) {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    @Override
    protected Workitem getWorkitem() {
        return workitem;
    }

    public boolean isRowChanged(int rowIndex) {
        boolean result = false;
        if (isWorkitemSet()) {
            result = workitem.isPropertyChanged(getRowSettings(rowIndex).attribute);
        }
        return result;
    }

    protected Configuration.ColumnSetting getRowSettings(int rowIndex) {
        return getWorkitemData()[rowIndex];
    }

    public int getPropertiesCount() {
        return getWorkitemData().length;
    }

    private Configuration.ColumnSetting[] getWorkitemData() {
        if (workitemData == null) {
            final Configuration.ColumnSetting[] columns = configuration.getColumns(getWorkitem().getType());
            final List<Configuration.ColumnSetting> workitemData = new ArrayList<Configuration.ColumnSetting>(columns.length);
            for (Configuration.ColumnSetting column : columns) {
                if (!column.effortTracking || ApiDataLayer.getInstance().isTrackEffortEnabled()) {
                    workitemData.add(column);
                }
            }
            this.workitemData = workitemData.toArray(new Configuration.ColumnSetting[workitemData.size()]); 
        }
        return workitemData;
    }
}