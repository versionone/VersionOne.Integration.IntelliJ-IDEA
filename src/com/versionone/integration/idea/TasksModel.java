/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;


import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.Workitem;

import com.intellij.util.ui.treetable.TreeTableModel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
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

    protected static final Map<String, Class> cTypes = new HashMap<String, Class>();

    public TasksModel(List<PrimaryWorkitem> data) {
        super("root");
        update(data);
        configuration = Configuration.getInstance();
        cTypes.put(Configuration.AssetDetailSettings.STRING_TYPE, String.class);
        cTypes.put(Configuration.AssetDetailSettings.LIST_TYPE, JComboBox.class);
        cTypes.put(Configuration.AssetDetailSettings.EFFORT_TYPE, String.class);
        cTypes.put(Configuration.AssetDetailSettings.MULTI_VALUE_TYPE, String.class);
        cTypes.put(Configuration.AssetDetailSettings.RICH_TEXT_TYPE, String.class);
    }

    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return TreeTableModel.class;
        }
	    return cTypes.get(getColumnSettings(columnIndex).type);
    }

    public int getColumnCount() {
        return getPropertiesCount();
    }

    public String getColumnName(int columnIndex) {
        return ApiDataLayer.getInstance().localizerResolve(getColumnSettings(columnIndex).name);
    }

    public Object getValueAt(Object node, int columnIndex) {
        if (node instanceof Workitem) {
            return ((Workitem)node).getProperty(getColumnSettings(columnIndex).attribute);
        }
        
        return null;
    }

    public void update(List<PrimaryWorkitem> data) {
        workitems = data;
    }

    public void setValueAt(Object aValue, Object node, int column) {
        // TODO
        System.out.println(aValue.toString());
    }

    public boolean isChanged(Object workitem) {
        Workitem item = (Workitem) workitem;
        return item.hasChanges();
    }

    protected Configuration.ColumnSetting getColumnSettings(int columnIndex) {
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

    public TableCellEditor getCellEditor(int row, int col, Object workitem) {
        Workitem item = (Workitem) workitem;
        Configuration.ColumnSetting settings = getColumnSettings(col);

        if (settings.type.equals(Configuration.AssetDetailSettings.STRING_TYPE)  || settings.type.equals(Configuration.AssetDetailSettings.EFFORT_TYPE)) {
            return createTextField(settings.readOnly || item.isPropertyReadOnly(settings.attribute));
        } else if (getColumnSettings(col).type.equals(Configuration.AssetDetailSettings.LIST_TYPE)) {
            final PropertyValues values = getAvailableValuesAt(row, col, item);
            final JComboBox comboEditor = new JComboBox(values.toArray());

            //select current value
            comboEditor.setSelectedItem(getValueAt(row, col));
            comboEditor.setBorder(null);
            return new DefaultCellEditor(comboEditor);
        }

        return createTextField(true);
    }

    private PropertyValues getAvailableValuesAt(int rowIndex, int columnIndex, Workitem item) {
        Configuration.ColumnSetting column = getColumnSettings(rowIndex);
        if (columnIndex == 1) {
            return ApiDataLayer.getInstance().getListPropertyValues(item.getType(), column.attribute);
        }
        return null;
    }

    private DefaultCellEditor createTextField(boolean isReadOnly) {
        final JTextField textField = new JTextField();
        textField.setEditable(!isReadOnly);
        textField.setEnabled(true);
        textField.setFocusable(true);
        textField.setBorder(new LineBorder(Color.black));
        
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Copy");
        menuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textField.copy();
            }
        });
        menu.add(menuItem1);
        textField.add(menu);

        MouseListener popupListener = new ContextMenuMouseListener(menu);
        textField.addMouseListener(popupListener);

        return new DefaultCellEditor(textField);
    }
}
