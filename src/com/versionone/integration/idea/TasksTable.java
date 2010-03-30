/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.util.ui.treetable.TreeTable;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.DataLayerException;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TasksTable extends TreeTable {
    private final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
    private final IDataLayer dataLayer;
    private final TasksModel treeTableModel;
    private final JPopupMenu contextMenu;

    public TasksTable(TasksModel treeTableModel, IDataLayer dataLayer) {
        super(treeTableModel);
        this.dataLayer = dataLayer;
        this.treeTableModel = treeTableModel;
        this.contextMenu = new JPopupMenu();
        WorkItemTreeTableCellRenderer treeCellRenderer = new WorkItemTreeTableCellRenderer();
        getTree().setCellRenderer(treeCellRenderer);
        //getTree().setCellEditor(new TreeCellEditor2());
        //setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor2(createTableRenderer(treeTableModel)));

        //createContextMenu();
    }

    private void createContextMenu() {
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        contextMenu.add(copyMenuItem);
        this.addMouseListener(new ContextMenuMouseListener(contextMenu));
    }

    public void updateData() throws DataLayerException {
        treeTableModel.update(dataLayer.getWorkitemTree());
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        
        if (rowIndex != getSelectedRow()) {
            TasksModel model = getTableModel();

            if (model.isChanged(getWorkitemAtRow(rowIndex))) {
                c.setBackground(colorsScheme.getColor(ColorKey.find("V1_CHANGED_ROW")));
                c.setForeground(Color.black);
            } else {
                c.setBackground(getBackground());
                c.setForeground(getForeground());
            }
        } else {
            c.setBackground(getSelectionBackground());
            c.setForeground(getSelectionForeground());
        }

        return c;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int col) {
        TableCellEditor editor = getTableModel().getCellEditor(row, col, getWorkitemAtRow(row));

        if(editor == null) {
            editor = super.getCellEditor(row, col);
        }

        return editor;
    }

    @Override
    public TasksModel getTableModel() {
        return (TasksModel) super.getTableModel();
    }

    protected Object getWorkitemAtRow(int rowIndex) {
        return getTree().getPathForRow(rowIndex).getLastPathComponent();
    }
}