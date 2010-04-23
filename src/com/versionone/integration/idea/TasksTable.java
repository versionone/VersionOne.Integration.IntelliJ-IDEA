/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.util.ui.treetable.TreeTable;
import com.intellij.util.ui.treetable.TreeTableModel;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.SecondaryWorkitem;
import com.versionone.integration.idea.actions.ContextMenuActionListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TasksTable extends TreeTable implements IContextMenuOwner {
    private final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
    private final IDataLayer dataLayer;
    private final TasksModel treeTableModel;

    public static final String CONTEXT_MENU_CLOSE = "Close...";
    public static final String CONTEXT_MENU_QUICK_CLOSE = "Quick close";
    public static final String CONTEXT_MENU_SIGNUP = "Sign me up";
    public static final String CONTEXT_MENU_CREATE_DEFECT = "Add new Defect";
    public static final String CONTEXT_MENU_CREATE_TASK = "Add new Task";
    public static final String CONTEXT_MENU_CREATE_TEST = "Add new Test";

    public TasksTable(@NotNull TasksModel model, IDataLayer dataLayer) {
        super(model);
        this.dataLayer = dataLayer;
        this.treeTableModel = model;

        JPopupMenu contextMenu = new JPopupMenu();
        WorkItemTreeTableCellRenderer treeCellRenderer = new WorkItemTreeTableCellRenderer();
        getTree().setCellRenderer(treeCellRenderer);

        ContextMenuMouseListener contextMenuMouseListener = new ContextMenuMouseListener(contextMenu, this);
        addMouseListener(contextMenuMouseListener);
    }

    /**
     * Re-read data from Data Layer and update everything.
     * @throws DataLayerException
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
        if (itemAtNode.getType().isPrimary()) {
            path = new TreePath(new Object[]{"root", itemAtNode});
        } else {
            path = new TreePath(new Object[]{"root", ((SecondaryWorkitem)itemAtNode).parent, itemAtNode});
            getTree().expandPath(path);
        }
        getTableModel().reload(new DefaultMutableTreeNode(itemAtNode));

        addSelectedPath(path);        
        scrollRectToVisible(getCellRect(getSelectedRow(), 0, false));
        updateUI(false);
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
    public List<ContextMenuItemWrapper> getMenuItemsAt(int x, int y) {
        int rowIndex = rowAtPoint(new Point(x, y));
        Workitem item = (Workitem) getWorkitemAtRow(rowIndex);
        List<ContextMenuItemWrapper> items = new ArrayList<ContextMenuItemWrapper>();

        if(item == null) {
            throw new UnsupportedOperationException("Cannot get menu items for non existing workitem row");
        }

        ContextMenuActionListener listener = new ContextMenuActionListener(item, this, dataLayer);
        JMenuItem closeMenuItem = new JMenuItem(CONTEXT_MENU_CLOSE);
        closeMenuItem.setEnabled(item.isPersistent());
        JMenuItem quickCloseMenuItem = new JMenuItem(CONTEXT_MENU_QUICK_CLOSE);
        quickCloseMenuItem.setEnabled(item.canQuickClose() && item.isPersistent());
        JMenuItem signupMenuItem = new JMenuItem(CONTEXT_MENU_SIGNUP);
        signupMenuItem.setEnabled(item.canSignup() && !item.isMine() && item.isPersistent());
        JMenuItem createDefectMenuItem = new JMenuItem(CONTEXT_MENU_CREATE_DEFECT);
        JMenuItem createTaskMenuItem = new JMenuItem(CONTEXT_MENU_CREATE_TASK);
        createTaskMenuItem.setEnabled(item.getType().isPrimary());
        JMenuItem createTestMenuItem = new JMenuItem(CONTEXT_MENU_CREATE_TEST);
        createTestMenuItem.setEnabled(item.getType().isPrimary());

        setMenuItemListener(listener, closeMenuItem, quickCloseMenuItem, signupMenuItem,
                createDefectMenuItem, createTaskMenuItem, createTestMenuItem);

        items.add(ContextMenuItemWrapper.createFromMenuItem(closeMenuItem));
        items.add(ContextMenuItemWrapper.createFromMenuItem(quickCloseMenuItem));
        items.add(ContextMenuItemWrapper.createSeparator());
        items.add(ContextMenuItemWrapper.createFromMenuItem(signupMenuItem));
        items.add(ContextMenuItemWrapper.createSeparator());
        items.add(ContextMenuItemWrapper.createFromMenuItem(createDefectMenuItem));
        items.add(ContextMenuItemWrapper.createFromMenuItem(createTaskMenuItem));
        items.add(ContextMenuItemWrapper.createFromMenuItem(createTestMenuItem));

        return items;
    }

    private void setMenuItemListener(ContextMenuActionListener listener, JMenuItem... menuItems) {
        for(JMenuItem menuItem : menuItems) {
            menuItem.addActionListener(listener);
        }
    }
}