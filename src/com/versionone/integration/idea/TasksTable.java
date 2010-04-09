/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.util.ui.treetable.TreeTable;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.IDataLayer;
import com.versionone.common.sdk.Workitem;
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

    public TasksTable(TasksModel treeTableModel, IDataLayer dataLayer) {
        super(treeTableModel);
        this.dataLayer = dataLayer;
        this.treeTableModel = treeTableModel;
        JPopupMenu contextMenu = new JPopupMenu();
        WorkItemTreeTableCellRenderer treeCellRenderer = new WorkItemTreeTableCellRenderer();
        getTree().setCellRenderer(treeCellRenderer);

        ContextMenuMouseListener contextMenuMouseListener = new ContextMenuMouseListener(contextMenu, this);
        addMouseListener(contextMenuMouseListener);
    }

    public void updateData() throws DataLayerException {
        treeTableModel.update(dataLayer.getWorkitemTree());
    }

    public void reloadNode(Object itemAtNode) {
        getTableModel().reload(new DefaultMutableTreeNode(itemAtNode));
    }

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
    public List<JMenuItem> getMenuItemsAt(int x, int y) {
        int rowIndex = rowAtPoint(new Point(x, y));
        Workitem item = (Workitem) getWorkitemAtRow(rowIndex);
        ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();

        ContextMenuActionListener listener = new ContextMenuActionListener(item, this);
        JMenuItem closeMenuItem = new JMenuItem(CONTEXT_MENU_CLOSE);
        JMenuItem quickCloseMenuItem = new JMenuItem(CONTEXT_MENU_QUICK_CLOSE);
        quickCloseMenuItem.setEnabled(item.canQuickClose());
        JMenuItem signupMenuItem = new JMenuItem(CONTEXT_MENU_SIGNUP);
        signupMenuItem.setEnabled(item.canSignup());
        setMenuItemListener(listener, closeMenuItem, quickCloseMenuItem, signupMenuItem);

        items.add(closeMenuItem);
        items.add(quickCloseMenuItem);
        items.add(signupMenuItem);

        return items;
    }

    private void setMenuItemListener(ContextMenuActionListener listener, JMenuItem... menuItems) {
        for(JMenuItem menuItem : menuItems) {
            menuItem.addActionListener(listener);
        }
    }
}