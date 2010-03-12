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
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.Workitem;

import com.intellij.util.ui.treetable.TreeTableModel;

import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import java.util.Vector;
import java.util.Date;
import java.util.List;

public class TasksModel extends AbstractTreeTableModel implements TreeTableModel {

    private static final TasksProperties[] propertiesWithEffort =
            {TITLE, ID, PARENT, DETAIL_ESTIMATE, DONE, EFFORT, TO_DO, STATUS};
    private static final TasksProperties[] properties =
            {TITLE, ID, PARENT, DETAIL_ESTIMATE, TO_DO, STATUS};

    private List<PrimaryWorkitem> workitems;

    static protected Class[] cTypes = {TreeTableModel.class, Integer.class, String.class, String.class, String.class, String.class, String.class, String.class};

    public TasksModel(List<PrimaryWorkitem> data) {
        super("root");
        workitems = data;
    }

    public Class getColumnClass(int column) {
	    return cTypes[column];
    }

    public int getColumnCount() {
        return getPropertiesCount();
    }

    public String getColumnName(int columnIndex) {
        return getProperty(-1, columnIndex).columnName;
    }

    public Object getValueAt(Object node, int column) {
        if (node instanceof Workitem) {
            return ((Workitem)node).getProperty(getProperty(-1, column).propertyName);
        }
        
        return null;
    }

    public void setValueAt(Object aValue, Object node, int column) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRowChanged(int row) {
        return false;
    }

    protected TasksProperties getProperty(int rowIndex, int columnIndex) {
        //return ((ApiDataLayer)root).isTrackEffortEnabled() ? propertiesWithEffort[columnIndex] : properties[columnIndex];
        return propertiesWithEffort[columnIndex];
    }

    public int getPropertiesCount() {
        //return ((ApiDataLayer)root).isTrackEffortEnabled() ? propertiesWithEffort.length : properties.length;
        return properties.length;
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
        Object[] data = getChildren(parent);
        if (index < data.length) {
            return data[index];
        }

        return new Object();
    }

    public int getChildCount(Object parent) {
        return getChildren(parent).length;
    }
}
