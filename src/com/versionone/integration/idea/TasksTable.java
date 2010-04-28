/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.util.ui.treetable.TreeTable;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.SecondaryWorkitem;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;

public class TasksTable extends TreeTable implements IContextMenuOwner {
    private final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
    private final IDataLayer dataLayer;
    private final TasksModel treeTableModel;

    public TasksTable(@NotNull TasksModel model, IDataLayer dataLayer) {
        super(model);
        this.dataLayer = dataLayer;
        this.treeTableModel = model;

        WorkitemTreeTableCellRenderer treeCellRenderer = new WorkitemTreeTableCellRenderer();
        getTree().setCellRenderer(treeCellRenderer);

        ContextMenuMouseListener contextMenuMouseListener = new ContextMenuMouseListener(this);
        addMouseListener(contextMenuMouseListener);
    }

    /**
     * Re-read data from Data Layer and update everything.
     * @throws DataLayerException if enlisting workitems is failed.
     */
    public void updateData() throws DataLayerException {
        List<PrimaryWorkitem> data = dataLayer.getWorkitemTree();
        treeTableModel.setHideColumns(false);
        treeTableModel.update(data);
    }

    /**
     * Redraw table and tree.
     * @param recreateColumns force recreating columns from model if true, skip this step if not.
     */
    public void updateUI(boolean recreateColumns) {
        getTree().revalidate();
        getTree().updateUI();

        if(recreateColumns) {
            createDefaultColumnsFromModel();
        }
        
        revalidate();
        repaint();
    }

    /**
     * Reload node, assuming that the underlying object is changed and we want to force displaying these changes.
     * @param itemAtNode corresponding workitem.
     */
    public void reloadNode(Workitem itemAtNode) {
        JTree tree = getTree();
        TreePath path = tree.getSelectionPath();
        getTableModel().reload(new DefaultMutableTreeNode(itemAtNode));

        if(itemAtNode.getType().isSecondary()) {
            tree.expandPath(path);
        }

        tree.scrollPathToVisible(path);
        tree.setSelectionPath(path);
        updateUI(false);
    }

    /**
     * Select node in the workitem list.
     * @param itemAtNode Workitem itemAtNode.
     */
    public void selectNode(Workitem itemAtNode) {
        if (itemAtNode == null) {
            return;
        }

        TreePath path;
        DefaultMutableTreeNode newNode;

        if (itemAtNode.getType().isPrimary()) {
            path = new TreePath(new Object[]{"root", itemAtNode});
            newNode = new DefaultMutableTreeNode(itemAtNode);
        } else {
            PrimaryWorkitem parent = ((SecondaryWorkitem)itemAtNode).parent;
            path = new TreePath(new Object[]{"root", parent, itemAtNode});
            TreePath pathToLeaf = new TreePath(new Object[]{"root", parent});
            newNode = new DefaultMutableTreeNode(itemAtNode);
            newNode.setParent(new DefaultMutableTreeNode(parent));
            getTree().expandPath(pathToLeaf);
        }

        getTableModel().reload(newNode);

        setSelectedPath(path);
        scrollRectToVisible(getCellRect(getSelectedRow(), 0, false));
        updateUI(false);
    }

    public void setSelectedPath(TreePath path) {
        int row = getTree().getRowForPath(path);
        getTree().setSelectionRow(row);
        getSelectionModel().setSelectionInterval(row, row);
    }

    /**
     * Reload model, causing data source changes to be displayed in UI
     */
    public void reloadModel() {
        getTableModel().reload();
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
        TreePath path = getTree().getPathForRow(rowIndex);
        return path != null ? path.getLastPathComponent() : null;
    }

    @NotNull
    public JPopupMenu getPopupMenu() {
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actions = (ActionGroup) ActionManager.getInstance().getAction("V1.ToolWindow.ContextMenu");
        ActionPopupMenu menu = actionManager.createActionPopupMenu("V1.ToolWindow.ContextMenu", actions);
        return menu.getComponent();
    }
}