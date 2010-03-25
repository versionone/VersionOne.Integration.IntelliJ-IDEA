/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;


import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.Workitem;

import com.intellij.util.ui.treetable.TreeTableModel;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TasksModel extends AbstractTreeTableModel {
    /*
     * Info about column for workitem (lazy)
     */
    private Configuration.ColumnSetting[] workitemData;
    protected final Configuration configuration;


    private List<PrimaryWorkitem> workitems;

    //static protected Class[] cTypes = {TreeTableModel.class, Integer.class, String.class, String.class, String.class, String.class, String.class, String.class};
    protected static final Map<String, Class> cTypes = new HashMap<String, Class>();

    public TasksModel(List<PrimaryWorkitem> data) {
        super("root");
        workitems = data;
        configuration = Configuration.getInstance();
        cTypes.put(Configuration.AssetDetailSettings.STRING_TYPE, String.class);
        cTypes.put(Configuration.AssetDetailSettings.LIST_TYPE, JComboBox.class);
        cTypes.put(Configuration.AssetDetailSettings.EFFORT_TYPE, String.class);
        cTypes.put(Configuration.AssetDetailSettings.MULTI_VALUE_TYPE, String.class);
        cTypes.put(Configuration.AssetDetailSettings.RICH_TEXT_TYPE, String.class);
    }

    public Class getColumnClass(int column) {
        if (column == 0) {
            return TreeTableModel.class;
        }
	    return cTypes.get(getProperty(-1, column).type);
    }

    public int getColumnCount() {
        return getPropertiesCount();
    }

    public String getColumnName(int columnIndex) {
        return ApiDataLayer.getInstance().localizerResolve(getProperty(-1, columnIndex).name);//getProperty(-1, columnIndex).columnName;
    }

    public Object getValueAt(Object node, int column) {
        if (node instanceof Workitem) {

            return ((Workitem)node).getProperty(getProperty(-1, column).attribute);
        }
        
        return null;
    }

    public void setValueAt(Object aValue, Object node, int column) {
        System.out.println(aValue.toString());
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRowChanged(int row) {
        return false;
    }

    protected Configuration.ColumnSetting getProperty(int rowIndex, int columnIndex) {
        return getWorkitemData()[columnIndex];
    }

    public int getPropertiesCount() {
        return getWorkitemData().length;
    }


    protected Object[] getChildren(Object parent) {
        if (parent == null || parent.equals("root")) {
            return workitems.toArray();
        } else if (parent instanceof PrimaryWorkitem) {
            return ((PrimaryWorkitem)parent).children.toArray();
        }

        return new Object[]{};
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof Object[]) {
            return ((Object[])parent)[index];
        }

        Object[] data = getChildren(parent);
        if (index < data.length) {
            return data[index];
        }

        return new Object();
    }

    public int getChildCount(Object parent) {
        if (parent instanceof Object[]) {
            return ((Object[])parent).length;
        }
        return getChildren(parent).length;
    }

    private Configuration.ColumnSetting[] getWorkitemData() {
        if (workitemData == null) {
            final Configuration.ColumnSetting[] columns = configuration.getColumnsForMainTable();
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
